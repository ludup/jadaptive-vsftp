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
	});