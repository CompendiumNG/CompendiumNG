<?php

/**
 * The following functions are meant to be used directly in template files.
 * They always echo.
 */

/* = General Template Tags
-------------------------------------------------------------- */

function the_qa_menu() {
	global $user_ID;
	$menu = array();
	
	if ( ($user_ID == 0 && qa_visitor_can('read_questions')) || current_user_can( 'read_questions' )) {
		$menu[] = array(
				'title' => __( 'Browse Suggestions', QA_TEXTDOMAIN ),
				'type' => 'archive',
				'current' => !is_qa_page( 'unanswered' ) && !is_qa_page( 'ask' ) && !is_qa_page( 'edit' )
			);

	}
	
	if ( ($user_ID == 0 && qa_visitor_can('publish_questions')) || current_user_can( 'publish_questions' )) {
		$menu[] = array(
			'title' => __( 'Make a new Suggestion', QA_TEXTDOMAIN ),
			'type' => 'ask',
			'current' => is_qa_page( 'ask' )
		);
	}
	$menu = apply_filters( 'qa_modify_menu_items', $menu );
	
	echo apply_filters( 'qa_before_menu', '' );
	
	echo "<div id='qa-menu'>";

	echo "<ul>";
	echo apply_filters( 'qa_first_menu_item', '' );
	foreach ( $menu as $item ) {
		extract( $item );

		$url = qa_get_url( $type );

		$id = $current ? 'qa-current-url' : '';

		echo _qa_html( 'li', array( 'id' => $id ),
			_qa_html( 'a', array( 'href' => $url ),
				$title
			)
		);
	}
	echo apply_filters( 'qa_last_menu_item', '' );
	echo "<li class='qa-search'>";
	echo "<b>Search here for existing suggesstions:</b>";
	the_qa_search_form();
	echo "</li>";
	echo "</ul>";

	
	
	echo apply_filters( 'qa_after_menu', '' );
	
	echo "</div>";
}

function the_qa_error_notice() {
	if ( !isset( $_GET['qa_error'] ) )
		return;
?>
	<div id="qa-error-notice">
		<?php _e( 'An error has occured while processing your submission.', QA_TEXTDOMAIN ); ?>
	</div>
<?php
}

function the_qa_search_form() {
?>
<form method="get" action="<?php echo qa_get_url('archive'); ?>">
	<input type="text" name="s" value="<?php the_search_query(); ?>" />
	<button><?php _e( 'Search', QA_TEXTDOMAIN ); ?></button>
</form>
<?php
}

function the_qa_pagination( $query = null ) {
	if ( is_null( $query ) )
		$query = $GLOBALS['wp_query'];

	if ( $query->max_num_pages <= 1 )
		return;

	$current_page = max( 1, $query->get( 'paged' ) );
	$total_pages = $query->max_num_pages;

	$padding = 2;
	$range_start = max( 1, $current_page - $padding );
	$range_finish = min( $total_pages, $current_page + $padding );

	echo '<div class="qa-pagination">';

	if ( $current_page > 1 )
		_qa_single_page_link( $query, $current_page - 1, __( 'prev', QA_TEXTDOMAIN ), 'prev' );

	if ( $range_start > 1 )
		_qa_single_page_link( $query, 1 );

	if ( $range_start > $padding )
		echo '<span class="dots">...</span>';

	foreach ( range( $range_start, $range_finish ) as $num ) {
		if ( $num == $current_page )
			echo _qa_html( 'span', array( 'class' => 'current' ), number_format_i18n( $num ) );
		else
			_qa_single_page_link( $query, $num );
	}

	if ( $range_finish + $padding <= $total_pages )
		echo '<span class="dots">...</span>';

	if ( $range_finish < $total_pages )
		_qa_single_page_link( $query, $total_pages );

	if ( $current_page < $total_pages )
		_qa_single_page_link( $query, $current_page + 1, __( 'next', QA_TEXTDOMAIN ), 'next' );

	echo '</div>';
}

function _qa_single_page_link( $query, $num, $title = '', $class = '' ) {
	if ( !$title )
		$title = number_format_i18n( $num );

	$args = array( 'href' => get_pagenum_link( $num ) );

	if ( $class )
		$args['class'] = $class;

	echo apply_filters( 'qa_single_page_link', _qa_html( 'a', $args, $title ) );
}

function the_qa_time( $id ) {
	$post = get_post( $id );

	$time = get_post_time( 'G', true, $post );

	$time_diff = time() - $time;

	if ( $time_diff > 0 && $time_diff < 24*60*60 )
		$h_time = sprintf( __( '%s ago', QA_TEXTDOMAIN ), human_time_diff( $time ) );
	else
		$h_time = mysql2date( get_option( 'date_format' ), $post->post_date );
	
	$h_time = apply_filters( 'qa_time', $h_time, $time);
	echo '<span class="qa-timediff">' . $h_time . '</span>';
}

function the_qa_author_box( $id ) {
	$user_id = get_post_field( 'post_author', $id );
?>
<div class="qa-user-box">
	<?php echo get_avatar( $user_id, 32 ); ?>
	<div class="qa-user-details">
		<?php the_qa_user_link( $user_id ); ?>
	</div>
</div>
<?php
}

function the_qa_action_links( $id ) {
	$links = array();

	$links['single'] = __( 'link', QA_TEXTDOMAIN );

	if ( current_user_can( 'edit_post', $id ) )
		$links['edit'] = __( 'edit', QA_TEXTDOMAIN );

	if ( current_user_can( 'delete_post', $id ) )
		$links['delete'] = __( 'delete', QA_TEXTDOMAIN );

	foreach ( $links as $type => $title )
		$links[ $type ] = _qa_html( 'a', array( 'href' => qa_get_url( $type, $id ) ), $title );

	echo '<div class="qa-action-links">';
	echo implode( ' | ', $links );
	echo '</div>';
}

function the_qa_user_link( $user_id ) {
	$author_name = get_the_author_meta( 'display_name', $user_id );
	$author_url = qa_get_url( 'user', $user_id );

	echo apply_filters( 'qa_user_link', "<a class='qa-user-link' href='$author_url'>$author_name</a>");
}

function the_qa_user_rep( $user_id ) {
?>
	<div class="qa-user-rep"><?php echo number_format_i18n( qa_get_user_rep( $user_id ) ); ?></div>
<?php
}

/* = Question Template Tags
-------------------------------------------------------------- */

function the_question_link( $question_id = 0 ) {
	if ( !$question_id )
		$question_id = get_the_ID();

	echo get_question_link( $question_id );
}

function get_question_link( $question_id ) {
	return apply_filters( 'qa_get_question_link', _qa_html( 'a', array( 'class' => 'question-link', 'href' => qa_get_url( 'single', $question_id ) ), get_the_title( $question_id ) ) );
}

function the_question_score( $question_id = 0 ) {
	if ( !$question_id )
		$question_id = get_the_ID();

	list( $up, $down ) = qa_get_votes( $question_id );

	$score = $up - $down;
	$score = apply_filters( 'qa_question_score', $score);
	
	echo "<div class='question-score'>";
	echo	"<div class='mini-count'>" . number_format_i18n( $score ) . "</div>";
	echo	"<div>" . _n( 'vote', 'votes', $score, QA_TEXTDOMAIN ) . "</div>";
	echo "</div>";
}

function the_question_voting( $question_id = 0 ) {
	global $_qa_core;

	if ( !$question_id )
		$question_id = get_the_ID();

	list( $up, $down, $current ) = qa_get_votes( $question_id );

	$buttons = array(
		'up' => __( 'This is a great idea/suggestion I like to support (click again to undo)', QA_TEXTDOMAIN )
	);

	foreach ( $buttons as $type => $text ) {
		$buttons[ $type ] = $GLOBALS['_qa_votes']->get_link( $question_id, $type, $current, $text );
	}

?>
<div class="qa-voting-box">
	<?php echo $buttons['up']; ?>
	<span title="<?php _e( 'Score', QA_TEXTDOMAIN ); ?>"><?php echo number_format_i18n( $up - $down ); ?></span>
	<?php echo $buttons['down']; ?>
</div>
<?php
}

function the_answer_voting( $answer_id ) {
	list( $up, $down, $current ) = qa_get_votes( $answer_id );

   	$buttons = array(
		
	);

	foreach ( $buttons as $type => $text ) {
		$buttons[ $type ] = $GLOBALS['_qa_votes']->get_link( $answer_id, $type, $current, $text );
	}

?>

<?php
}

function the_answer_accepted( $answer_id ) {
	$question_id = get_post_field( 'post_parent', $answer_id );

	$user_can_accept = get_post_field( 'post_author', $question_id ) == get_current_user_id();

	$is_accepted = get_post_meta( $question_id, '_accepted_answer', true ) == $answer_id;

	if ( $user_can_accept ) {
		$data = array(
			'action' => 'qa_accept',
			'answer_id' => $answer_id,
			'accept' => ( $is_accepted ? 'off' : 'on' )
		);
?>
<form method="post" action="">
	<?php wp_nonce_field( 'qa_accept' ); ?>

	<?php foreach ( $data as $key => $value ) {
		echo _qa_html( 'input', array( 'type' => 'hidden', 'name' => $key, 'value' => $value ) );
	} ?>

	<?php echo _qa_html( 'input', array(
		'type' => 'submit',
		'title' => __( 'Accept answer (click again to undo)', QA_TEXTDOMAIN ),
		'class' => 'vote-accepted-' . ( $is_accepted ? 'on' : 'off' )
	) ); ?>
</form>
<?php
	}
	elseif ( $is_accepted ) {
		echo _qa_html( 'span', array(
			'title' => __( 'Accepted answer', QA_TEXTDOMAIN ),
			'class' => 'vote-accepted-on'
		), __( 'accepted', QA_TEXTDOMAIN ) );
	}
}

function the_question_status( $question_id = 0 ) {
	if ( !$question_id )
		$question_id = get_the_ID();

	$count = get_answer_count( $question_id );

	if ( get_post_meta( $question_id, '_accepted_answer', true ) )
		$status = 'answered-accepted';
	elseif ( $count > 0 )
		$status = 'answered';
	else
		$status = 'unanswered';
		
	$status = apply_filters( 'qa_question_status', $status );

	echo "<div class='question-status $status'>";
	echo	"<div class='mini-count'>" . number_format_i18n( $count ) . "</div>";
	echo	"<div>" . _n( 'comment', 'comments', $count, QA_TEXTDOMAIN ) . "</div>";
	echo "</div>";
}

function the_question_tags( $before = '', $sep = ', ', $after = '' ) {
	the_terms( 0, 'question_tag', $before, $sep, $after );
}

function the_question_category( $before = '', $sep = ', ', $after = '' ) {
	the_terms( 0, 'question_category', $before, $sep, $after );
}

function the_question_form() {
	global $wp_query, $wp_version, $qa_general_settings;

	if ( is_qa_page( 'edit' ) ) {
		$question = $wp_query->posts[0];

		if ( !current_user_can( 'edit_question', $question->ID ) )
			return;

		$question->tags = wp_get_object_terms( $question->ID, 'question_tag', array( 'fields' => 'names' ) );

		$cats = wp_get_object_terms( $question->ID, 'question_category', array( 'fields' => 'ids' ) );
		$question->cat = empty( $cats ) ? false : reset( $cats );
	} else {
		$question = (object) array(
			'ID' => '',
			'post_content' => '',
			'post_title' => '',
			'tags' => array(),
			'cat' => false
		);
	}
	
	

?>
<form id="question-form" method="post" action="<?php echo qa_get_url( 'archive' ); ?>">
	<?php wp_nonce_field( 'qa_edit' ); ?>

	<input type="hidden" name="qa_action" value="edit_question" />
	<input type="hidden" name="question_id" value="<?php echo esc_attr( $question->ID ); ?>" />

	<table id="question-form-table">
		<tr>
			<td id="question-title-label">
				<label for="question-title"><?php _e('<b>Suggestion:</b>', QA_TEXTDOMAIN); ?></label>
			</td>
			<td id="question-title-td">
				<input type="text" id="question-title" name="question_title" value="<?php echo esc_attr( $question->post_title ); ?>" />
			</td>
		</tr>
		<tr><th colspan="2"><br />Provide a more detailed description of your suggestion:</th></tr>
	</table>
	
	<?php
	$use_editor = true;
	if ( isset( $qa_general_settings["disable_editor"] ) && $qa_general_settings["disable_editor"] )
		$use_editor = false;

	if ( version_compare($wp_version, "3.3") >= 0 && $use_editor ) {
		$wp_editor_settings = apply_filters( 'qa_question_editor_settings', array(), $question->ID );
		wp_editor( $question->post_content, 'question_content', $wp_editor_settings );
	} else {
	?>
	<textarea name="question_content" class="wp32"><?php echo esc_textarea( $question->post_content ); ?></textarea>
	<?php } ?>

	<table id="question-taxonomies">
		<!--tr>
			<td id="question-tags-label">
				<label for="question-tags"><?php _e('Tags:', QA_TEXTDOMAIN); ?></label>
			</td>
			<td id="question-tags-td">
				<input type="text" id="question-tags" name="question_tags" value="<?php echo implode( ', ', $question->tags ); ?>" />
			</td>
		</tr-->
	</table>

	<?php the_qa_submit_button(); ?>
</form>
<?php
}

/* = Answer Template Tags
-------------------------------------------------------------- */

function the_answer_link( $answer_id ) {
	$question_id = get_post_field( 'post_parent', $answer_id );

	echo _qa_html( 'a', array( 'class' => 'answer-link', 'href' => qa_get_url( 'single', $answer_id ) ), get_the_title( $question_id ) );
}

function the_answer_count( $question_id = 0 ) {
	$count = get_answer_count( $question_id ? $question_id : get_the_ID() );

	printf( _n( '1 Comment', '%d Comments', $count, QA_TEXTDOMAIN ), number_format_i18n( $count ) );
}

function the_answer_list() {
	global $user_ID;
	$question_id = get_the_ID();

	if ( ($user_ID == 0 && !qa_visitor_can('read_answers', $question_id)) && !current_user_can( 'read_answers', $question_id ) )
		return;

	$accepted_answer = get_post_meta( $question_id, '_accepted_answer', true );

	$answers = new WP_Query( array(
		'post_type' => 'answer',
		'post_parent' => $question_id,
		'post__not_in' => array( $accepted_answer ),
		'orderby' => 'qa_score',
		'posts_per_page' => QA_ANSWERS_PER_PAGE,
		'paged' => get_query_var( 'paged' )
	) );

	if ( $accepted_answer && !get_query_var( 'paged' ) )
		array_unshift( $answers->posts, get_post( $accepted_answer ) );

	the_qa_pagination( $answers );

	foreach ( $answers->posts as $answer ) {
		setup_postdata( $answer );
?>
	<div id="answer-<?php echo $answer->ID; ?>" class="answer">
		<?php the_answer_voting( $answer->ID ); ?>
		<div class="answer-body">
			<div class="answer-content">
				<?php the_content(); ?>
			</div>

			<div class="answer-meta">
				<?php the_qa_action_links( $answer->ID ); ?>
				<?php the_qa_author_box( $answer->ID ); ?>
			</div>
		</div>
	</div>
<?php
	}

	the_qa_pagination( $answers );

	wp_reset_postdata();
}

function the_answer_form() {
	global $wp_query, $user_ID, $wp_version, $qa_general_settings;
	
	if ( is_qa_page( 'edit' ) ) {
		$answer = $wp_query->posts[0];
		
		if ( ($user_ID == 0 && !qa_visitor_can('edit_published_answers', $answer->ID)) && !current_user_can( 'edit_published_answers', $answer->ID ) )
			return;
	} else {
		if ( ($user_ID == 0 && !qa_visitor_can('publish_answers')) && !current_user_can( 'publish_answers') ) {
			echo '<p>'.__('You are not allowed to add answers!', QA_TEXTDOMAIN).'</p>';
			return;
		}
		$answer = (object) array(
			'ID' => '',
			'post_parent' => get_the_ID(),
			'post_content' => ''
		);
	}
	
	
?>
<form id="answer-form" method="post" action="<?php echo qa_get_url( 'archive' ); ?>">
	<?php wp_nonce_field( 'qa_answer' ); ?>

	<input type="hidden" name="qa_action" value="edit_answer" />
	<input type="hidden" name="question_id" value="<?php echo esc_attr( $answer->post_parent ); ?>" />
	<input type="hidden" name="answer_id" value="<?php echo esc_attr( $answer->ID ); ?>" />

	<?php 
	$use_editor = true;
	if ( isset( $qa_general_settings["disable_editor"] ) && $qa_general_settings["disable_editor"] )
		$use_editor = false;
	
	if (version_compare($wp_version, "3.3") >= 0 && $use_editor ) { 
		$wp_editor_settings = apply_filters( 'qa_answer_editor_settings', array(), $answer->ID ); ?>
		<p><?php wp_editor(  $answer->post_content, 'answer', $wp_editor_settings); ?></p>
	<?php } else { ?>
		<p><textarea name="answer" class="wp32"><?php echo esc_textarea( $answer->post_content ); ?></textarea></p>
	<?php } ?>
	<?php the_qa_submit_button(); ?>
</form>
<?php
}

function the_qa_submit_button() {
	global $qa_general_settings;
	if ( is_user_logged_in() ) {
		$button = __( 'Submit', QA_TEXTDOMAIN );
	} elseif ( get_option( 'users_can_register' ) ) {
		$button = __( 'Register/Login and Submit', QA_TEXTDOMAIN );
	} else {
		$button = __( 'Login and Submit', QA_TEXTDOMAIN );
	}
?>
	<input class="qa-edit-submit" type="submit" value="<?php echo $button; ?>" />
<?php
}

function qa_visitor_can($capability, $post_id = null) {
	return false;
}

