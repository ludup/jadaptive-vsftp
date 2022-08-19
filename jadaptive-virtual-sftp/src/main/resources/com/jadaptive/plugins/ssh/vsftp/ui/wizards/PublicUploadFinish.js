 $(function() {
	 Wizard.initStep('publicUploadWizard', function(success, failure) {
		 success();
	 });
	 
	 $('#finishButton').click(function(e) {
		 Wizard.finish();
	 });
 });