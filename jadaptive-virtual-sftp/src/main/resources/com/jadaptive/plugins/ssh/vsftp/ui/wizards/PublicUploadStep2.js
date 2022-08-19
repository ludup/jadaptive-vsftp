$(function() {
	 
	   $('input[name="type"]').change(function() {
			var idx;
			var url = window.location.href;
			if((idx = url.indexOf('?')) > -1) {
				url = url.substring(0, idx);
			}
			window.location = url + "?type=" + $(this).val(); 	
		});
	
		 Wizard.initStep('publicUploadWizard', function(success, cancel) {
			 
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