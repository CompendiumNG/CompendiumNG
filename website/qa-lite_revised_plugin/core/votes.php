<?php

/**
 * Takes care of voting and reputation.
 */
class QA_Votes {

	function QA_Votes() {
		add_action( 'template_redirect', array( &$this, 'handle_voting' ), 9 );
		add_action( 'template_redirect', array( &$this, 'handle_accepting' ), 9 );
		add_action( 'transition_post_status', array( &$this, 'update_reps' ), 10, 3 );
		add_action( 'bp_before_member_header_meta', array( &$this, 'bp_before_member_header_meta' ), 10, 3 );

		if ( !is_admin() )
			add_filter( 'posts_clauses', array( &$this, 'posts_clauses' ), 10, 2 );
	}
	
	function bp_before_member_header_meta() {
		global $bp;
		
		echo sprintf(__('<span class="qa qa-rep activity">QA rep %d</span>', QA_TEXTDOMAIN), number_format_i18n( qa_get_user_rep(  bp_displayed_user_id() ) ));
	}

	/**
	 * Get a vote link to something.
	 *
	 * @param int $id Object id
	 * @param string $vote_type Type of vote: 'up' or 'down'
	 * @param string $vote_type Current vote, if any
	 * @param string $link_text The content of the link
	 * @return string
	 */
	function get_link( $id, $vote_type, $current_vote, $link_text ) {
		$voted = ( $vote_type == $current_vote );

		if ( !$voted ) {
			$vote = $vote_type;
		} else {
			$vote = 'undo';
		}

		$data = array(
			'action' => 'qa_vote',
			'post_id' => $id,
			'vote_type' => $vote
		);

		$input_attr = array(
			'type' => 'submit',
			'title' => $link_text,
			'class' => "vote-$vote_type-" . ( $voted ? 'on' : 'off' )
		);

		if ( !is_user_logged_in() )
			$input_attr['data-msg'] = 'login';
		elseif ( get_post_field( 'post_author', $id ) == get_current_user_id() )
			$input_attr['data-msg'] = 'own';

		ob_start();
?>
<form method="post" action="">
	<?php wp_nonce_field( 'qa_vote' ); ?>

	<?php foreach ( $data as $key => $value ) {
		echo _qa_html( 'input', array( 'type' => 'hidden', 'name' => $key, 'value' => $value ) );
	} ?>

	<?php echo _qa_html( 'input', $input_attr ); ?>
</form>
<?php
		return ob_get_clean();
	}

	/**
	 * Add a vote to something.
	 *
	 * @param int $id Object id
	 * @param string $vote Type of vote: 'up' or 'down'
	 * @return bool True on success, False on failure
	 */
	function add( $id, $vote ) {
		$author = get_post_field( 'post_author', $id );

		$user_id = get_current_user_id();

		// Don't allow votes on one's own content
		if ( $author == $user_id )
			return false;

		// Check capability
		if ( !current_user_can( 'read', $id ) )
			return;

		$this->remove( $id, false );

		if ( 'up' == $vote ) {
			add_post_meta( $id, '_up_vote', $user_id );
		} elseif ( 'down' == $vote ) {
			add_post_meta( $id, '_down_vote', $user_id );
		} else {
			return false;
		}

		$this->update_reps_after_vote( $id, $user_id );

		return true;
	}

	/**
	 * Remove a vote from something.
	 *
	 * @param int $id Object id
	 * @param bool $recount Wether to update the user rep or not
	 */
	function remove( $id, $recount = true ) {
		$user_id = get_current_user_id();

		delete_post_meta( $id, '_up_vote', $user_id );
		delete_post_meta( $id, '_down_vote', $user_id );

		if ( $recount ) {
			$this->update_reps_after_vote( $id, $user_id );
		}
	}

	/**
	 * Get votes for something.
	 *
	 * @param int $id Object id
	 * @return int|array Vote count or array of vote counts and vote by the current user
	 */
	function get( $id ) {
		$up = get_post_meta( $id, '_up_vote', array() );
		$down = get_post_meta( $id, '_down_vote', array() );

		$user_id = get_current_user_id();

		if ( in_array( $user_id, $up ) )
			$current = 'up';
		elseif ( in_array( $user_id, $down ) )
			$current = 'down';
		else
			$current = false;

		return array( count( $up ), count( $down ), $current );
	}

	/**
	 * Populates $wp_query->posts objects with the 'qa_score' property
	 */
	function posts_clauses( $clauses, $wp_query ) {
		global $wpdb;

		$post_types = array_intersect( (array) $wp_query->get( 'post_type' ), array( 'question', 'answer' ) );

		if ( empty( $post_types ) || 'none' == $wp_query->get('qa_count') )
			return $clauses;

		$clauses['fields'] .= ", COUNT(up.meta_value) - COUNT(down.meta_value) AS qa_score";
		$clauses['join'] .= "
			LEFT JOIN $wpdb->postmeta up ON ($wpdb->posts.ID = up.post_id AND up.meta_key = '_up_vote')
			LEFT JOIN $wpdb->postmeta down ON ($wpdb->posts.ID = down.post_id AND down.meta_key = '_down_vote')
		";
		$clauses['groupby'] = "$wpdb->posts.ID";

		if ( isset( $wp_query->query['orderby'] ) && 'qa_score' == $wp_query->query['orderby'] )
			$clauses['orderby'] = "qa_score DESC, post_date ASC";

		return $clauses;
	}

	/**
	 * Handle voting and unvoting.
	 */
	function handle_voting() {
		if ( !isset( $_POST['action'] ) || 'qa_vote' != $_POST['action'] )
			return;

		if ( !isset( $_POST['_wpnonce'] ) || !wp_verify_nonce( $_POST['_wpnonce'], 'qa_vote' ) )
			return;

		$id = $_POST['post_id'];

		$vote_type = $_POST['vote_type'];

		if ( 'undo' == $vote_type )
			$this->remove( $id );
		else
			$this->add( $id, $vote_type );
	}

	/**
	 * Handle accepting answers.
	 */
	function handle_accepting() {
		if ( !isset( $_POST['action'] ) || 'qa_accept' != $_POST['action'] )
			return;

		if ( !isset( $_POST['_wpnonce'] ) || !wp_verify_nonce( $_POST['_wpnonce'], 'qa_accept' ) )
			return;

		$answer_id = (int) $_POST['answer_id'];

		$question_id = get_post_field( 'post_parent', $answer_id );

		if ( 'on' == $_POST['accept'] ) {
			update_post_meta( $question_id, '_accepted_answer', $answer_id );
		} else {
			delete_post_meta( $question_id, '_accepted_answer' );
		}

		$this->update_user_rep( get_post_field( 'post_author', $id ) );
		$this->update_user_rep( get_post_field( 'post_author', $question_id ) );
	}

	/**
	 * Update user reps when a post is unpublished.
	 */
	function update_reps( $new_status, $old_status, $post ) {
		if (
			!in_array( $post->post_type, array( 'question', 'answer' ) )
			|| 'publish' != $old_status || $new_status == $old_status
		)
			return;

		$this->update_user_rep( $post->post_author );

		// TODO: if it's a question, also update answer authors
	}

	/**
	 * Update all the relevant user reputations after a certain vote
	 *
	 * @param int $id Post id
	 * @param int $user_id User id
	 */
	function update_reps_after_vote( $id, $user_id ) {
		$to_update = array( $user_id, get_post_field( 'post_author', $id ) );
		$this->update_user_rep( $to_update );
	}

	/**
	 * Calculates the number of points a certain user has and stores it in usermeta
	 *
	 * @param int|array $user_id User id or list of such
	 */
	function update_user_rep( $user_ids ) {
		global $_qa_core, $wpdb;

		$user_ids = array_filter( array_unique( array_map( 'absint', (array) $user_ids ) ) );

		foreach ( $user_ids as $user_id ) {
			$total = 0;

			// Calculate bonuses for accepted answers
			$accepted_answers = $wpdb->get_var( $wpdb->prepare( "
				SELECT COUNT(*)
				FROM $wpdb->posts q
				INNER JOIN $wpdb->postmeta m ON (q.ID = m.post_id AND meta_key = '_accepted_answer')
				WHERE post_type = 'question'
				AND post_status = 'publish'
				AND meta_value IN (
					SELECT ID
					FROM $wpdb->posts
					WHERE post_type = 'answer'
					AND post_status = 'publish'
					AND post_author = %d
				)
				AND post_author != %d
			", $user_id, $user_id ) );

			$total += $accepted_answers * QA_ANSWER_ACCEPTED;

			// Calculate bonuses for accepting answers
			$accepting_answers = $wpdb->get_var( $wpdb->prepare( "
				SELECT COUNT(*)
				FROM $wpdb->posts q
				INNER JOIN $wpdb->postmeta m ON (q.ID = m.post_id AND meta_key = '_accepted_answer')
				WHERE post_type = 'question'
				AND post_status = 'publish'
				AND meta_value NOT IN (
					SELECT ID
					FROM $wpdb->posts
					WHERE post_type = 'answer'
					AND post_status = 'publish'
					AND post_author = %d
				)
				AND post_author = %d
			", $user_id, $user_id ) );

			$total += $accepting_answers * QA_ANSWER_ACCEPTING;

			// Count up votes on user's answers
			$up_votes_by_others = $_qa_core->get_count( array(
				'post_type' => 'answer',
				'author' => $user_id,
				'meta_key' => '_up_vote'
			) );

			$total += $up_votes_by_others * QA_ANSWER_UP_VOTE;

			// Count up votes on user's questions
			$up_votes_by_others = $_qa_core->get_count( array(
				'post_type' => 'question',
				'author' => $user_id,
				'meta_key' => '_up_vote'
			) );

			$total += $up_votes_by_others * QA_QUESTION_UP_VOTE;

			// Count down votes on user's questions and answers
			$down_votes_by_others = $_qa_core->get_count( array(
				'post_type' => array( 'question', 'answer' ),
				'author' => $user_id,
				'meta_key' => '_down_vote'
			) );

			$total += $down_votes_by_others * QA_DOWN_VOTE;

			// Calculate penalty for downvoting answers
			$down_votes_by_user = $_qa_core->get_count( array(
				'post_type' => 'answer',
				'meta_key' => '_down_vote',
				'meta_value' => $user_id
			) );

			$total += $down_votes_by_user * QA_DOWN_VOTE_PENALTY;

			update_user_meta( $user_id, '_qa_rep', $total );
		}
	}
}

$GLOBALS['_qa_votes'] = new QA_Votes();

