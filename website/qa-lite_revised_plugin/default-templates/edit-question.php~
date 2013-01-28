<?php get_header( 'question' ); ?>

<div id="qa-page-wrapper">
    <div id="qa-content-wrapper">
    <?php do_action( 'qa_before_content', 'edit-question' ); ?>
    
    <?php the_qa_menu(); ?>
    
    <div id="edit-question">
    <?php the_question_form(); ?>
    </div>
    
    <?php do_action( 'qa_after_content', 'edit-question' ); ?>
    </div>
</div><!--#qa-page-wrapper-->

<?php 
global $qa_general_settings;

if ( !isset( $qa_general_settings["full_width"] ) || !$qa_general_settings["full_width"] )	
	get_sidebar( 'question' ); 
?>

<?php get_footer( 'question' ); ?>

