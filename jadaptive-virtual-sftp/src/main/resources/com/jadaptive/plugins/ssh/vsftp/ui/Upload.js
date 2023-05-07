const validateEmail = (email) => {
  return email.match(
    /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
  );
};
	
$(function() {
	UploadWidget.init('/upload/public/' + $('#uploadHolder').attr("jad:shortCode"), '/app/ui/upload-complete/' + $('#uploadHolder').attr("jad:shortCode"), "#feedback", function(fd) {
		
		var name = $('#name').val().trim();
		var email = $('#email').val().trim();
		var reference = $('#reference').val().trim();
		
		fd.append("name", name);
		fd.append("email", email);
		fd.append("reference", reference);
	}, function() {
		
		var name = $('#name').val();
		var email = $('#email').val();
		var reference = $('#reference').val();
		
		if(name.trim() === '' || email.trim() === '' || reference.trim() === '' || !validateEmail(email.trim())) {
			$('#feedback').append('<p class="alert alert-danger"><i class="fa-solid fa-exclamation-circle"></i> Please provide your name, valid email address and a reference for your files.</p>');
			return false;
		}

		return true;
	});
});