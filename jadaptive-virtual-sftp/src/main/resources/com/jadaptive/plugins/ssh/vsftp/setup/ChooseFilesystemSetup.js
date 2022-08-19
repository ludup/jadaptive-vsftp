$(function() {
	
		 $('.choose').change(function(e) {
			 debugger;
			$('.choose').prop('checked', false);
			$(this).prop('checked', true);
			$('#filesystemType').val($(this).val());
		 });
		 
		 $('.choose').each(function(idx, obj) {
			 if($(this).val() == $('#filesystemType').val()) {
				 $(this).prop('checked', true);
			 }
		 });
		 
		 Wizard.initStep('setup', function(success, cancel) {
		 
		 $.ajax({
	           type: "POST",
	           url: $('form').attr('action'),
	           cache: false,
	           contentType: false,
	    	   processData: false,
	           data: new FormData($("form")[0]),
	           dataType: 'json',
	           success: function(data)
	           {
	           	   	if(data.success) {
	           			success();
	           	   	} else {
	           	    	JadaptiveUtils.error($('#feedback'), data.message);
	           	   	}
	           },
	           complete: function() {
					cancel();
	           }
	      });

	 });
 });