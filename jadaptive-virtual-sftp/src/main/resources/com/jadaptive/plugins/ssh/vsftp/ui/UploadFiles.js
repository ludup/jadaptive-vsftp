$(function() {
	
	var path = window.location.pathname.replace('/app/ui/upload-files', '');
	debugger;
	
			$('#breadcrumb').empty();
		$('#breadcrumb').append('<li class="breadcrumb-item"><a class="clickPath" href="/app/ui/tree/"><i class="far fa-hdd"></i></a></li>');
		var lastIdx = path.indexOf('/');
		
		while(lastIdx < path.length-1) {
		
			var idx = path.indexOf('/', lastIdx+1);
			if(idx==-1) {
				idx = path.length;
			}
			$('#breadcrumb').append('<li class="breadcrumb-item"><a class="clickPath" href="/app/ui/tree' 
					+ path.substring(0,idx) + '">' + path.substring(lastIdx+1, idx) + '</a></li>');
			lastIdx = idx;
		}
		$('.breadcrumb-item').last().addClass('active');
		
	UploadWidget.init('/upload/files', '/app/ui/tree' + path, "#feedback", function(fd) {
			fd.append("path", path);
	});

});
