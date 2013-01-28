<?php

define( 'QA_BP_COMPONENT_SLUG', 'q-and-a' );

class QA_BuddyPress {

	function QA_BuddyPress() {
		global $bp;
		
		add_filter( 'qa_get_url', array( &$this, 'qa_get_url' ), 10, 3 );
		
		add_action( 'bp_setup_nav', array( &$this, 'bp_setup_nav'), 100 );
		add_action( 'template_redirect', array( &$this, 'template_redirect' ), 9 );
		add_filter( 'bp_activity_can_comment', array( &$this, 'hide_comment' ) ); // New in V1.2
	}
	
	/**
	 * Hides comment button for Buddypress Activity Stream
	 * @Since V1.2
	 */
	function hide_comment( $can_comment ){
		global $activities_template, $qa_general_settings;
		
		if ( !$activities_template || !@$qa_general_settings["bp_comment_hide"] )
			return $can_comment;
 
		$activity = $activities_template->activity;
	  
		if ( 'qa' != $activity->component )
			return $can_comment;
		else
			return false;
	} 
	
	function bp_setup_nav() {
		bp_core_new_nav_item( array(
			'name' => __( 'Q&A', QA_TEXTDOMAIN ),
			'slug' => QA_BP_COMPONENT_SLUG,
			'screen_function' => array( &$this, 'tab_template' ),
			'default_subnav_slug' => 'questions'
		) );
	}

	// Make the BP profile URL canonical
	function template_redirect() {
		if ( is_qa_page( 'user' ) ) {
			wp_redirect( $this->_get_url( get_query_var( 'author' ) ), 301 );
			die;
		}
	}

	// Replace normal /questions/user/* URL
	function qa_get_url( $url, $type, $id ) {
		if ( 'user' == $type )
			return $this->_get_url( $id );

		return $url;
	}

	function _get_url( $user_id ) {
		return bp_core_get_user_domain( $user_id ) . QA_BP_COMPONENT_SLUG . '/';
	}

	function tab_template() {
		global $_qa_core;
		
		add_action( 'bp_template_content', array( &$this, 'tab_content' ) );
		add_action( 'bp_member_plugin_options_nav', array( &$this, 'tab_nav' ) );
		add_filter( 'is_qa_page', array( &$this, 'is_qa_page' ), 10, 2 );
		
		$_qa_core->load_default_style();
		
		bp_core_load_template( 'members/single/plugins' );
	}

	function is_qa_page( $result, $type ) {
		if ( 'user' == $type || !$type )
			return true;

		return $result;
	}
	
	function tab_nav() {
		$user_id = bp_displayed_user_id();
		
		$question_query = new WP_Query( array(
			'author' => $user_id,
			'post_type' => 'question',
			'posts_per_page' => 20,
			'update_post_term_cache' => false
		) );

		$answer_query = new WP_Query( array(
			'author' => $user_id,
			'post_type' => 'answer',
			'posts_per_page' => 20,
			'update_post_term_cache' => false
		) );

		$fav_query = new WP_Query( array(
			'post_type' => 'question',
			'meta_key' => '_fav',
			'meta_value' => $user_id,
			'posts_per_page' => 20,
		) );
?>
		<li><a href="#qa-user-questions">
			<span id="user-questions-total"><?php echo number_format_i18n( $question_query->found_posts ); ?></span>
			<?php echo _n( 'Question', 'Questions', $question_query->found_posts, QA_TEXTDOMAIN ); ?>
		</a></li>

		<li><a href="#qa-user-answers">
			<span id="user-answers-total"><?php echo number_format_i18n( $answer_query->found_posts ); ?></span>
			<?php echo _n( 'Answer', 'Answers', $answer_query->found_posts, QA_TEXTDOMAIN ); ?>
		</a></li>
<?php
	}

	function tab_content() {
		$user_id = bp_displayed_user_id();

		$question_query = new WP_Query( array(
			'author' => $user_id,
			'post_type' => 'question',
			'posts_per_page' => 20,
			'update_post_term_cache' => false
		) );

		$answer_query = new WP_Query( array(
			'author' => $user_id,
			'post_type' => 'answer',
			'posts_per_page' => 20,
			'update_post_term_cache' => false
		) );

		$fav_query = new WP_Query( array(
			'post_type' => 'question',
			'meta_key' => '_fav',
			'meta_value' => $user_id,
			'posts_per_page' => 20,
		) );
?>
	<div id="qa-user-questions">
		<div id="question-list">
		<?php while ( $question_query->have_posts() ) : $question_query->the_post(); ?>
			<div class="question">
				<div class="question-stats">
					<?php the_question_score(); ?>
					<?php the_question_status(); ?>
				</div>
				<div class="question-summary">
					<h3><?php the_question_link(); ?></h3>
					<?php the_question_tags(); ?>
					<div class="question-started">
						<?php the_qa_time( get_the_ID() ); ?>
					</div>
				</div>
			</div>
		<?php endwhile; ?>
		</div><!--#question-list-->
	</div><!--#qa-user-questions-->

	<div id="qa-user-answers">
		<ul>
		<?php
			while ( $answer_query->have_posts() ) : $answer_query->the_post();
				list( $up, $down ) = qa_get_votes( get_the_ID() );

				echo '<li>';
					echo "<div class='answer-score'>";
					echo number_format_i18n( $up - $down );
					echo "</div> ";
					the_answer_link( get_the_ID() );
				echo '</li>';
			endwhile;
		?>
		</ul>
	</div><!--#qa-user-answers-->
<?php
	}
}

$GLOBALS['_qa_buddypress'] = new QA_BuddyPress;
