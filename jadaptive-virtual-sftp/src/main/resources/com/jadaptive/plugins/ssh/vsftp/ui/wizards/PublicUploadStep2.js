$(function() {
	 
	   $('input[name="type"]').change(function() {
			var idx;
			var url = window.location.href;
			if((idx = url.indexOf('?')) > -1) {
				url = url.substring(0, idx);
			}
			window.location = url + "?type=" + $(this).val(); 	
		});

 });