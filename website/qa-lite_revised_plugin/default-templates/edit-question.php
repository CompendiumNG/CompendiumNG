<?php get_header( 'question' ); ?>

<div id="qa-page-wrapper">
    <div id="qa-content-wrapper">
    <?php do_action( 'qa_before_content', 'edit-question' ); ?>
    
      <br /><h1 class="post_title">Feature Suggestions Page (FSP)</h1><hr width="100%" />
      <p>Please provide a succinct summary of your suggestion, as well as a more detailed description of your idea, 
      why you think it is important and how it will improve the use of CompendiumNG for others as well.</p> 
      <hr width="100%" />
      
		<?php the_qa_menu(); ?>	
      <hr width="100%" />	
    
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

