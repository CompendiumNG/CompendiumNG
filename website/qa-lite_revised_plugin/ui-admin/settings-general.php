<?php if (!defined('ABSPATH')) die('No direct access allowed!'); ?>
<?php
	if (!current_user_can('manage_options')) {
		wp_die( __('You do not have sufficient permissions to access this page.') );
	}
?>

<?php
global $wp_roles, $qa_email_notification_subject, $qa_email_notification_content;
$options = $this->get_options('general_settings');
$wp_nonce_verify = wp_nonce_field('qa-verify', '_wpnonce', true, false);
?>

<div class="wrap">
	<?php screen_icon('options-general'); ?>

	<h2><?php _e( 'Q&A Settings', QA_TEXTDOMAIN ); ?></h2>
	<div id="poststuff" class="metabox-holder">
	
	<form action="" method="post" class="qa-general">

	<div class="postbox <?php echo $this->postbox_classes('qa_display') ?>" id="qa_display">
	<h3 class='hndle'><span><?php _e('Display Settings', QA_TEXTDOMAIN) ?></span></h3>
	
	
	<div class="inside">

		<table class="form-table">
		
			<tr>
			<td colspan="2">
			<span class="description">
			<?php _e( 'These settings will only be applied to Q&A pages and they are optional. If you are not having any display issues you can leave them as they are.', QA_TEXTDOMAIN ) ?>
			</span>
			</td>
			</tr>
		
			<tr>
				<th>
					<label for="page_width"><?php _e( 'Page width (px)', QA_TEXTDOMAIN ) ?></label>
				</th>
				<td>
					<input style="width:100px" name="page_width" value="<?php echo @$options['page_width']; ?>" />
					&nbsp;&nbsp;&nbsp;
					<span class="description">
					<?php _e( 'This setting will only be applied to the default template. Does not include sidebar width.', QA_TEXTDOMAIN ) ?>
					</span>
				</td>
			</tr>

			<tr>
				<th>
					<label for="content_width"><?php _e( 'Content width (px)', QA_TEXTDOMAIN ) ?></label>
				</th>
				<td>
					<input style="width:100px" name="content_width" value="<?php echo @$options['content_width']; ?>" />
					&nbsp;&nbsp;&nbsp;
					<span class="description">
					<?php _e( 'This setting will only be applied to the default template.', QA_TEXTDOMAIN ) ?>
					</span>
				</td>
			</tr>
			
			<tr>
				<th>
					<label for="search_input_width"><?php _e( 'Search input width (px)', QA_TEXTDOMAIN ) ?></label>
				</th>
				<td>
					<input style="width:100px" name="search_input_width" value="<?php echo @$options['search_input_width']; ?>" />
					&nbsp;&nbsp;&nbsp;
					<span class="description">
					<?php _e( 'This setting will only be applied to the default template.', QA_TEXTDOMAIN ) ?>
					</span>
				</td>
			</tr>
			
			<tr>
				<th>
					<label for="additional_css"><?php _e( 'Additional css rules', QA_TEXTDOMAIN ) ?></label>
				</th>
				<td>
					<textarea cols="120" rows="2" name="additional_css"><?php echo @$options['additional_css']; ?></textarea>
					<br />
					<span class="description">
					<?php _e( 'This setting will only be applied to the default template. Use valid css. e.g.', QA_TEXTDOMAIN ) ?>&nbsp;<code>#sidebar{width:200px;float:left;}</code>
					</span>
				</td>
			</tr>
			
			<tr>
				<th>
					<label for="full_width"><?php _e( 'Full Width Pages', QA_TEXTDOMAIN ) ?></label>
				</th>
				<td>
					<input type="checkbox" name="full_width" <?php if ( @$options["full_width"] ) echo "checked='checked'"; ?> />
					&nbsp;&nbsp;&nbsp;
					<span class="description">
					<?php _e( 'This setting will only be applied to the default template. If your theme does not support sidebars, or you want full width question and answer pages check this checkbox.', QA_TEXTDOMAIN ); ?>
					</span>
				</td>
			</tr>
			
			<tr>
				<th>
					<label for="questions_per_page"><?php _e( 'Questions Per Page', QA_TEXTDOMAIN ) ?></label>
				</th>
				<td>
					<input style="width:100px" name="questions_per_page" value="<?php echo @$options['questions_per_page']; ?>" />&nbsp;&nbsp;&nbsp;<span class="description"><?php echo __( 'If left empty, WP setting will be used: ', QA_TEXTDOMAIN ) . get_option('posts_per_page'); ?></span>
					<br />
					<span class="description">
					<?php printf( __( 'IMPORTANT: Questions Per Page cannot be less than Wordpress %s setting, because of WP limitations. If you set it like that Wordpress setting will be used instead.', QA_TEXTDOMAIN ), '<a href="'.admin_url('options-reading.php'). '">'. __('Blog pages show at most', QA_TEXTDOMAIN )  . '</a>' ); ?>
					</span>
				</td>
			</tr>
			
			<tr>
				<th>
					<label for="answers_per_page"><?php _e( 'Answers Per Page', QA_TEXTDOMAIN ) ?></label>
				</th>
				<td>
					<input style="width:100px" name="answers_per_page" value="<?php echo @$options['answers_per_page']; ?>" />&nbsp;&nbsp;&nbsp;<span class="description"><?php _e( 'If left empty: 20', QA_TEXTDOMAIN ); ?></span>
				</td>
			</tr>
			
			<tr>
				<th>
					<label for="disable_editor"><?php _e( 'Disable WP Editor', QA_TEXTDOMAIN ) ?></label>
				</th>
				<td>
					<input type="checkbox" name="disable_editor" <?php if ( @$options["disable_editor"] ) echo "checked='checked'"; ?> />
					&nbsp;&nbsp;&nbsp;
					<span class="description">
					<?php _e( 'If you are having issues with Buddypress or if you don\'t want submissions to be formatted, check this checkbox. Then, textarea will be used for question and answer forms instead of the WP editor.', QA_TEXTDOMAIN ); ?>
					</span>
				</td>
			</tr>
			
		</table>
		</div>
		</div>

	<p class="submit">
		<?php echo $wp_nonce_verify; ?>
		<input type="hidden" name="action" value="qa-save" />
		<input type="hidden" name="key" value="general_settings" />
		<input type="submit" class="button-primary" name="save" value="<?php _e( 'Save Everything on this Page', QA_TEXTDOMAIN ); ?>">
		<img class="ajax-loader" src="<?php echo QA_PLUGIN_URL . 'ui-admin/images/ajax-loader.gif'; ?>" />
		<span style="display:none;font-weight:bold;color:darkgreen" class="qa_settings_saved"><?php _e( 'Settings saved', QA_TEXTDOMAIN ); ?></span>
	</p>
	
	<div class="postbox <?php echo $this->postbox_classes('qa_access') ?>" id="qa_access">
	<h3 class='hndle'><span><?php _e('Accessibility Settings', QA_TEXTDOMAIN) ?></span></h3>
	
	
	<div class="inside">

		<table class="form-table">
			
			<tr>
				<th>
					<label for="roles"><?php _e( 'Assign Capabilities', QA_TEXTDOMAIN ) ?></label>
					<img class="ajax-loader" src="<?php echo QA_PLUGIN_URL . 'ui-admin/images/ajax-loader.gif'; ?>" />
				</th>
				<td>
					<select id="roles" name="roles">
						<?php foreach ( $wp_roles->role_names as $role => $name ): ?>
							<option value="<?php echo $role; ?>"><?php echo $name; ?></option>
						<?php endforeach; ?>
					</select>
					<span class="description"><?php _e('Select a role to which you want to assign WP Q&A capabilities.', QA_TEXTDOMAIN); ?></span>

					<br /><br />

					<div id="capabilities">
						<?php foreach ( $GLOBALS['_qa_core_admin']->capability_map as $capability => $description ): ?>
							<input id="<?php echo $capability?>_checkbox" type="checkbox" name="capabilities[<?php echo $capability; ?>]" value="1" />
							<span class="description <?php echo $capability?>"><?php echo $description; ?></span>
							<br />
						<?php endforeach; ?>
					</div>
				</td>
			</tr>
			
			
			<tr>
				<th>
					<label for="thank_you_page"><?php _e( 'Thank You Page', QA_TEXTDOMAIN ) ?></label>
				</th>
					<td>
					<?php 
					if ( isset( $options['thank_you'] ) )
						$selected = $options['thank_you'];
					else
						$selected = 0;
					wp_dropdown_pages( array('name'=>'thank_you', 'selected'=>$selected) ); ?>
					<br />
					<span class="description">
					<?php _e( 'If questions are saved as pending, user will be redirected to this page after submitting a question.', QA_TEXTDOMAIN ) ?>
					</span>
					</td>
			</tr>
			
			<tr>
				<th>
					<label for="unauthorized"><?php _e( 'Unauthorized Access Page', QA_TEXTDOMAIN ) ?></label>
				</th>
				
				<td>
				<?php 
				if ( isset( $options['unauthorized'] ) )
					$selected = $options['unauthorized'];
				else
					$selected = 0;
				wp_dropdown_pages( array('name'=>'unauthorized', 'selected'=>$selected) ); ?>
				<br />
				<span class="description">
				<?php _e( 'If a user tries to access a page he should not access, he will be redirected to this page instead.', QA_TEXTDOMAIN ) ?>
				</span>
				</td>
			</tr>
			
			
			<?php
			global $bp;
			if ( is_object( $bp ) ) : 
			?>
			<tr>
				<th>
					<label for="bp_comment_hide"><?php _e( 'Disable Reply in Activity Stream', QA_TEXTDOMAIN ) ?></label>
				</th>
					<td>
					<input type="checkbox" name="bp_comment_hide" value="1" <?php if (@$options["bp_comment_hide"]) echo "checked='checked'" ?>/>
					&nbsp;&nbsp;&nbsp;
					<span class="description">
					<?php _e( 'Checking this will disable commenting for the question asked notification in Buddypress Activity Stream, forsing user to answer the question through plugin generated pages.', QA_TEXTDOMAIN ) ?>
					</span>
					</td>
			</tr>
			
			<?php endif; ?>
		</table>
		</div>
		</div>

	<p class="submit">
		<?php echo $wp_nonce_verify; ?>
		<input type="hidden" name="action" value="qa-save" />
		<input type="hidden" name="key" value="general_settings" />
		<input type="submit" class="button-primary" name="save" value="<?php _e( 'Save Everything on this Page', QA_TEXTDOMAIN ); ?>">
		<img class="ajax-loader" src="<?php echo QA_PLUGIN_URL . 'ui-admin/images/ajax-loader.gif'; ?>" />
		<span style="display:none;font-weight:bold;color:darkgreen" class="qa_settings_saved"><?php _e( 'Settings saved', QA_TEXTDOMAIN ); ?></span>
	</p>

	
	
	</form>
	</div>
</div>
