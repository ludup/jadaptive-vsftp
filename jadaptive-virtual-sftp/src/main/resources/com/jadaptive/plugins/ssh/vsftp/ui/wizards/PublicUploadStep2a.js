$(function() {
	 	
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