<?php

class QA_AJAX {

	function init() {
		add_action( 'wp_ajax_qa_vote', array( __CLASS__, 'vote' ) );
		add_action( 'wp_ajax_qa_accept', array( __CLASS__, 'accept' ) );
		
		add_action( 'wp_ajax_nopriv_qa_vote', array( __CLASS__, 'vote' ) );
		add_action( 'wp_ajax_nopriv_qa_accept', array( __CLASS__, 'accept' ) );
	}

	function vote() {
		global $_qa_votes;

		$_qa_votes->handle_voting();

		$id = $_POST['post_id'];
		$post_type = get_post_type( $id );

		if ( 'question' == $post_type )
			the_question_voting( $id );
		elseif ( 'answer' == $post_type )
			the_answer_voting( $id );
		else
			die( -1 );
		die;
	}

	function accept() {
		global $_qa_votes;

		$_qa_votes->handle_accepting();

		$id = $_POST['answer_id'];

		the_answer_accepted( $id );

		die;
	}
}

QA_AJAX::init();

