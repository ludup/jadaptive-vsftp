$(function() {

	UploadWidget.init('/upload/gpg', '/app/ui/search/gpgKey', "#feedback", function(fd) {
		}, function() {
			return true;
		});
		UploadWidget.multiple(false);
		UploadWidget.auto(true);
});