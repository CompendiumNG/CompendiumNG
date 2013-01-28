<?php

class QA_Subscriptions {

	function QA_Subscriptions() {
		add_action( 'template_redirect', array( &$this, 'handle_subs' ), 9 );
		add_action( 'transition_post_status', array( &$this, 'notify' ), 10, 3 );
	}

	function get_link( $id, $subscribe_text, $unsubscribe_text ) {
		if ( !is_user_logged_in() )
			return;

		// Question authors are automatically subscribed
		if ( get_current_user_id() == get_post_field( 'post_author', $id ) )
			return;

		$subscribers = get_post_meta( $id, '_sub' );

		$subscribed = in_array( get_current_user_id(), $subscribers );

		$url = add_query_arg( array(
			'qa_sub' => ( $subscribed ? '0' : '1' ),
			'_wpnonce' => wp_create_nonce( 'qa_sub' )
		), qa_get_url( 'single', $id ) );

		$attr = array(
			'class' => 'qa-subcribe-link',
			'href' => $url
		);

		return _qa_html( 'a', $attr, $subscribed ? $unsubscribe_text : $subscribe_text );
	}

	function handle_subs() {
		if ( !is_qa_page( 'single' ) )
			return;

		if ( !is_user_logged_in() )
			return;

		if ( !isset( $_GET['_wpnonce'] ) || !wp_verify_nonce( $_GET['_wpnonce'], 'qa_sub' ) )
			return;

		// Question authors are automatically subscribed
		if ( get_current_user_id() == get_post_field( 'post_author', get_queried_object_id() ) )
			return false;

		if ( $_GET['qa_sub'] )
			add_post_meta( get_queried_object_id(), '_sub', get_current_user_id() );
		else
			delete_post_meta( get_queried_object_id(), '_sub', get_current_user_id() );
	}

	// TODO: use wp-cron
	function notify( $new_status, $old_status, $post ) {
		global $current_site;
		
		if ( 'answer' != $post->post_type || 'publish' != $new_status || $new_status == $old_status )
			return;

		$author = get_userdata( $post->post_author );

		$question_id = $post->post_parent;
		$question = get_post($question_id);
		
		$subscribers = get_post_meta( $question_id, '_sub' );
		if ( !in_array( $question->post_author, $subscribers ) )
			$subscribers[] = $question->post_author; // Notify question author too

		$subject = sprintf( __( '[%s] New answer on "%s"' ), get_option( 'blogname' ), $question->post_title );

		$content = sprintf( __( '%s added a new answer to %s:', QA_TEXTDOMAIN ),
			_qa_html( 'a', array( 'href' => qa_get_url( 'user', $post->post_author ) ), $author->user_nicename ),
			_qa_html( 'a', array( 'href' => qa_get_url( 'single', $question_id ) ), get_post_field( 'post_title', $question_id ) )
		);

		$content .= "<br/><br/>" . $post->post_content . "<br/><br/>";

		cache_users( $subscribers );
		
		$admin_email = get_site_option('admin_email');
		if ($admin_email == ''){
			$admin_email = 'admin@' . $current_site->domain;
		}
		
		$from_email = $admin_email;
		$message_headers = "MIME-Version: 1.0\n" . "From: " . $current_site->site_name .  " <{$from_email}>\n" . "Content-Type: text/html; charset=\"" . get_option('blog_charset') . "\"\n";
		
		foreach ( $subscribers as $subscriber_id ) {
			// Don't notify the author of the answer
			if ( $post->post_author != $subscriber_id ) {
				$msg = $content . sprintf( __( 'To manage your subscription, visit <a href="%s">the question</a>.', QA_TEXTDOMAIN ), qa_get_url( 'single', $post->ID ) );
			} else {
				$msg = $content;
			}

			wp_mail( get_user_option( 'user_email', $subscriber_id ), $subject, $msg, $message_headers);
		}
	}
}

$GLOBALS['_qa_subscriptions'] = new QA_Subscriptions();

