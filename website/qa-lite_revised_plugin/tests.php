<?php

/*
 * Test suite; just drop this file in your mu-plugins folder
 */

function test_rewrites() {
	global $pagenow;
	
	if ( 'index.php' != $pagenow )
		return;

	$archives = array(
		qa_get_url( 'archive' ),
		qa_get_url( 'user' ),
	);

	$tag_id = (int) reset( get_terms( 'question_tag', array( 'fields' => 'ids' ) ) );
	if ( $tag_id )
		$archives[] = qa_get_url( 'tag', $tag_id );

	$urls = array(
		qa_get_url( 'ask' )
	);

	$question_id = reset( get_posts( array(
		'post_type' => 'question',
		'fields' => 'ids'
	) ) );

	if ( $question_id ) {
		$urls[] = qa_get_url( 'single', $question_id );
		$urls[] = qa_get_url( 'edit', $question_id );
	}

	$urls = array_merge( $urls,
		$archives

#		,array_map(function($url) {
#			return trailingslashit( $url ) . $GLOBALS['wp_rewrite']->pagination_base . '/2';			
#		}, $archives)

#		,array_map(function($url) {
#			return trailingslashit( $url ) . 'feed';			
#		}, $archives)
	);

	foreach ( $urls as $url ) {
		$class = ( '200' == wp_remote_retrieve_response_code( wp_remote_get( $url ) ) ) ? 'updated' : 'error';
		echo "<div class='$class'><p><a href='$url'>$url</a></p></div>";
	}
}
add_action('admin_notices', 'test_rewrites');

function test_templates() {
	foreach ( array( 'ask', 'edit', 'single', 'archive', 'tag', 'category' ) as $type ) {
		if ( is_qa_page( $type ) )
			echo( "<pre>$type: true\n</pre>" );
	}
}
#add_action( 'template_redirect', 'test_templates' );

