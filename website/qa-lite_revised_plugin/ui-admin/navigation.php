<?php if (!defined('ABSPATH')) die('No direct access allowed!'); ?>

<?php $settings_page = 'settings';  ?>

<h2>
    <?php if ( isset( $_GET['page'] ) && $_GET['page'] == $settings_page ): ?>
        <a class="nav-tab <?php if ( isset( $_GET['tab'] ) && $_GET['tab'] == 'general' || !isset( $_GET['tab'] ) )  echo 'nav-tab-active'; ?>" href="edit.php?post_type=question&page=<?php echo $settings_page; ?>&tab=general&sub=general"><?php _e( 'General', QA_TEXTDOMAIN ); ?></a>
    <?php endif; ?>

    <?php do_action('render_admin_navigation_tabs'); ?>
</h2>

<?php if ( isset( $_GET['page'] ) && $_GET['page'] == $settings_page && isset( $_GET['tab'] ) && $_GET['tab'] == 'general' || empty( $_GET['tab'] ) ): ?>
<ul>
    <li class="subsubsub"><h3><a class="<?php if ( isset( $_GET['sub'] ) && $_GET['sub'] == 'general' || empty( $_GET['tab'] ) )   echo 'current'; ?>" href="edit.php?post_type&page=<?php echo $settings_page; ?>&tab=general&sub=general"><?php _e( 'General Settings', QA_TEXTDOMAIN ); ?></a></h3></li>
</ul>
<?php endif; ?>

<?php do_action('render_admin_navigation_subs'); ?>

<div class="clear"></div>

<?php $this->render_admin( 'message' ); ?>
