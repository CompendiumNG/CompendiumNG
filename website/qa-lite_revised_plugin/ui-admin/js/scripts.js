jQuery(function($) {

	function populate_checkboxes() {
		$('.ajax-loader').show();
		// clear checked fields
		$('#capabilities input').attr( 'checked', false );
		// set data
		var data = {
			action: 'qa-get-caps',
			role: $('#roles option:selected').val()
		};
		// make the post request and process the response
		$.post(ajaxurl, data, function(response) {
			$('.ajax-loader').hide();
			$.each(response, function(index) {
				if ( index != null ) {
					$('input[name="capabilities[' + index + ']"]').attr( 'checked', true );
				}
			});
			qa_modify_opacity();
		});
	}
	function qa_modify_opacity() {
		if ( !$('#publish_questions_checkbox').is(':checked')) { 
			$(".immediately_publish_questions").css("opacity","0.3"); 
			$("#immediately_publish_questions_checkbox").attr('checked', false);
		}
		else { $(".immediately_publish_questions").css("opacity","1"); }
	}

	populate_checkboxes();
	qa_modify_opacity();

	$('#roles').change(populate_checkboxes);
	$("#publish_questions_checkbox").change(qa_modify_opacity);
		
	$('.qa-general').submit(function() {
		$('.ajax-loader').show();

		var data = $(this).serializeArray();

		$.post(ajaxurl, data, function() {
			$('.ajax-loader').hide();
			$('.qa_settings_saved').show().delay(5000).fadeOut('slow');
		});

		return false;
	});
	
});
