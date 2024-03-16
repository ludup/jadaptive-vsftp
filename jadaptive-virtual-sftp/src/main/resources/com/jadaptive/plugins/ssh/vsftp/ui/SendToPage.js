

$(function() {
	UploadWidget.init('/upload/send-to/' + $('#shareCode').val(), null, "#feedback", function(fd) {
		
		fd.append("shareCode", $('#shareCode').val());
		
	}, function(doUpload) {
		
		$('#waiting').removeClass('d-none');
		
		/**
		 * This is called to validate the upload. We will return false and wait
		 * for the receiver to connect. Once they connect we will initiate the
		 * transfer of the file(s).
		 */
		var poll = function() {
			
			$.getJSON('/app/api/sendTo/receiver/' + $('#shareCode').val() + '/' + UploadWidget.count(), function(data) {
			
				if(data.success) {
					$('#progressText').text("${virtualFolder:transferingFiles.text}");
					doUpload(function() {
						window.location.reload();
					}, function() {
						window.location = "/app/ui/error";
					});
				} else {
					setTimeout(poll, 1000);
				}
			});
		};
		
		setTimeout(poll, 1000);
		return false;
	});
});