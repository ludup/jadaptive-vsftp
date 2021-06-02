Object.defineProperty(Number.prototype,'fileSize',{value:function(a,b,c,d){
		 return (a=a?[1e3,'k','B']:[1024,'K','iB'],b=Math,c=b.log,
		 d=c(this)/c(a[0])|0,this/b.pow(a[0],d)).toFixed(2)
		 +' '+(d?(a[1]+'MGTPEZY')[--d]+a[2]:'Bytes');
		},writable:false,enumerable:false});
	
$(document).ready(function() {
	
	$('.dropzoneClick').click(function(e) {
		$('.file-input').last().trigger('click');
	});
	
	$(document).on('change', '.file-input', function(e) {
		var row = $(this).parents('.jfiles').find('tr').last();
		row.parent().append(row.clone());
		var fileInput = $('.file-input').last();
		fileInput.parent().append('<input class="file-input" type="file" name="file" style="display: none;" />');
		row.find(".filename").text(fileInput[0].files[0].name);
		row.find(".size").text((fileInput[0].files[0].size).fileSize(1));
		row.show();
		
		$('.jfiles').show();
	});
	
	$('#uploadForm').submit(function(e) {
		e.preventDefault();
		UploadWidget.upload();
	});
});

var UploadWidget = {
	
	_options: {},
	init: function(postUrl, successUrl, feedbackDiv, callback) {
		_self = this;
		_self._options.postUrl = postUrl;
		_self._options.successUrl = successUrl;
		_self._options.feedbackDiv = feedbackDiv ? feedbackDiv : "#uploadForm";
		_self._options.callback = callback;
	},
	clearFiles: function() {
		parent = $('.file-input').parent();
		$('.file-input').remove();
		parent.append('<input class="file-input" type="file" name="file" style="display: none;" />');
	},
    upload: function() {
	
			_self = this;

			$(_self._options.feedbackDiv + ' .alert').remove();
			
			if($('.file-input').length <= 1) {
				$(_self._options.feedbackDiv).prepend('<p class="alert alert-danger">There are no files selected!</p>');
				return;
			}
			$('.file-input').last().remove();
			
			JadaptiveUtils.startAwesomeSpin($('#uploadButton i', 'upload'));

			var fd = new FormData();
			if(_self._options.callback) {
				_self._options.callback(fd);
			}
			
			$('.file-input').each(function(idx, file) {
				fd.append('file', this.files[0]);
			});
		    
		    $.ajax({
		           type: "POST",
		           url: _self._options.postUrl,
		           dataType: "json",
		           contentType: false,
		           processData: false,
		           data: fd,
		           success: function(data)
		           {
		        	   if(data.success) {
		        		   window.location = _self._options.successUrl;
		        	   } else {
		        		   $(_self._options.feedbackDiv).prepend('<p class="alert alert-danger">' + data.message + '</p>');
							_self.clearFiles();
		        	   }
		           },
		           always: function() {
		        	   JadaptiveUtils.stopAwesomeSpin($('#uploadButton i', 'upload'));
		           }
		     });
    }
};


