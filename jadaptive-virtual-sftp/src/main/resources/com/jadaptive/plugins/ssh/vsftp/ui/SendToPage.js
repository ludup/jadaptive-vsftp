

$(function() {
	UploadWidget.init('/upload/send-to/' + $('#shareCode').val(), null, "#feedback", function(fd) {
		
		fd.append("shareCode", $('#shareCode').val());
		
	}, function(doUpload) {
		
		if(UploadWidget.count() == 0) {
			JadaptiveUtils.error($('#feedback'), '${virtualFolder:noFiles.text}');
			return false;
		}
		
		if($('#quota').data('enforcing')) {
			var remaining = $('#quota').data('quota');
			if(remaining < UploadWidget.size()) {
				JadaptiveUtils.error($('#feedback'), '${virtualFolder:quotaExceeded.text}');
				return false;
			}
		}
		
		$('.feedback').remove();
		
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