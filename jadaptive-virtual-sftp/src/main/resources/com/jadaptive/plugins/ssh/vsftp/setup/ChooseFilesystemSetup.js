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
		 
 });