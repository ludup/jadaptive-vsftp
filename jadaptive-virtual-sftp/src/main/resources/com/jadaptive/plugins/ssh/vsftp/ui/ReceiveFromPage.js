$(function() {
	
	setTimeout(function() {
		window.location = '/app/api/sendTo/recv/' + $('#shareCode').val();
	}, 2000);
	
	var poll = function() {
		
		$.getJSON('/app/api/sendTo/status/' + $('#shareCode').val(), function(data) {
			
			if(data.success) {
				$('#progressText').text("${virtualFolder:transferComplete.text}");
				$('#spinner').remove();
			} else {
				setTimeout(poll, 1000);
			}
		});

	};
	
	setTimeout(poll, 1000);
});