$(function() {
	$('#startButton').click(function(e) {
		e.preventDefault();
		Wizard.start('publicUploadWizard');
	});
});