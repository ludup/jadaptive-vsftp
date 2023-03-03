$(function() {
   $.get({  url: '/app/vfs/report/' + $('#uuid').val(),
			dataType: 'json',
			success: function(data) {
				if (data.success) {
					
					$('#content').empty();
					$('#content').removeClass("text-center");
					$('#content').append('<h1>Mount Name     ' + data.resource.name + '</h1>');
					$('#content').append('<h1>Mount Type     ' + data.resource.type + '</h1>');
					$('#content').append('<h1>Total Size     ' + data.resource.totalSize + '</h1>');
					$('#content').append('<h1>HTTP Downloads ' + data.resource.httpDownloads + '</h1>');
					$('#content').append('<h1>HTTP Uploads   ' + data.resource.httpUploads + '</h1>');
					$('#content').append('<h1>SCP Downloads  ' + data.resource.scpDownload + '</h1>');
					$('#content').append('<h1>SCP Uploads    ' + data.resource.scpUpload + '</h1>');
					$('#content').append('<h1>SFTP Downloads ' + data.resource.sftpDownload + '</h1>');
					$('#content').append('<h1>SFTP Uploads   ' + data.resource.sftpUpload + '</h1>');

				} else {
					JadaptiveUtils.error($('#feedback'), data.message);
				}
			}
		});	
});