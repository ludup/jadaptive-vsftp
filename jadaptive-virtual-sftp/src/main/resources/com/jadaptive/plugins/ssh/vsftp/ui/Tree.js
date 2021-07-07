	function renderPerms(val, obj) {
		var perms = obj.readable ? 'r' : '-';
		perms += obj.writable ? 'w': '-';
		return perms;
	}
	
	function renderName(val, obj) {
		var icon = (obj.directory ? '<i class="far fa-folder"></i>' : '<i class="far fa-file"></i>');
		if(obj.directory) {
			return '<a class="clickPath" href="' + obj.path + '">' +  icon + ' ' + obj.name + '</a><br><small>' + obj.path + '</small>';
		} else {
			return icon + ' ' + obj.name + '<br><small>' + obj.path + '</small>';
		}
	}
	
	function renderLength(val) {
		if(val > 0) {
			var i = Math.floor( Math.log(val) / Math.log(1024) );
		    return ( val / Math.pow(1024, i) ).toFixed(2) * 1 + ' ' + ['B', 'kB', 'MB', 'GB', 'TB'][i];
		}
		
		return '';
	}
	
	function renderActions(val, obj) {
		var html =  '<a class="deleteFile mr-1" href="#" data-name="' + obj.name + '" data-folder="' + obj.directory + '" data-path="' + obj.path + '"><i class="far fa-trash"></i></a>';
		if(!obj.directory) {
			html += '<a class="downloadFile mr-1" href="/app/vfs/downloadFile' + obj.path + '"><i class="far fa-download"></i></a>';
		}
		return html;
	}
	
	function getMaximumFiles() {
		var results = $('#maximumFiles').val();
		if(results.trim()==='') {
			return 1000;
		}
		return results;
	}
	
	function getBoolean(val) {
		if(val === 'true') {
			return true;
		}
		return false;
	}
	
	function getPath() {

		var path = $('#path').val();
		if(!path) {
			path = window.location.pathname.replace('/app/ui/tree', '');
			if(path==='') {
				path = '/';
			}
		}
		return path;
	}
	
	function ajaxRequest(params) {
		
		$('#feedback').empty();
		
		var path = getPath();
		$('#uploadFiles').attr('href', '/app/ui/upload-files' + path);
	    var url = '/app/vfs/listDirectory' + path;

	    params.data.filter = $('#filter').val();
	    params.data.files = $('#files').is(":checked");
	    params.data.folders = $('#folders').is(":checked");
	    params.data.hidden =$('#hidden').is(":checked");
	    params.data.maximumResults = getMaximumFiles();
	    params.data.searchDepth = $('#searchDepth').val();
	    
	    $.get(url + '?' + $.param(params.data)).then(function (res) {
	    	  
	    	$('table').show();
		      
	    	  if(res.success) {
		    	  params.success(res);
                  $('#path').val(path);
		      } else {
		    	  JadaptiveUtils.error($('#feedback'), data.message);
		    	  params.success({
		    		 rows: [],
		    		 total: 0
		    	  });
		      }

			updateBreadcrumb(path);
	    });
	  }
	
	function changePath(path) {
		
		$('#path').val(path);
		$('#uploadFiles').attr('href', '/app/ui/upload-files' + path);
		$('table').bootstrapTable('refresh');
		
}

function updateBreadcrumb(path) {
		$('#breadcrumb').empty();
		$('#breadcrumb').append('<li class="breadcrumb-item"><a class="clickPath" href="/"><i class="far fa-hdd"></i></a></li>');
		var lastIdx = path.indexOf('/');
		
		while(lastIdx < path.length-1) {
		
			var idx = path.indexOf('/', lastIdx+1);
			if(idx==-1) {
				idx = path.length;
			}
			$('#breadcrumb').append('<li class="breadcrumb-item"><a class="clickPath" href="' 
					+ path.substring(0,idx) + '">' + path.substring(lastIdx+1, idx) + '</a></li>');
			lastIdx = idx;
		}
		$('.breadcrumb-item').last().addClass('active');
		
	}
	
	function refresh() {
		var path = getPath();
		if(!path) {
			path = window.location.pathname.replace('/app/ui/tree', '');
			if(path==='') {
				path = '/';
			}
		}
		changePath(path);
	}

$(function() {
	$(document).on('click', '.clickPath', function(e) {
		e.preventDefault();
		changePath($(this).attr('href'));
	});
		
	$('#table').bootstrapTable({
		sidePagination: 'server',
		pagination: true,
		pageList: "[10, 25, 50, 100, 200, All]",
		pageSize: 10,
		pageNumber: 1,
		showRefresh: true,
		mobileResponsive: true,
		ajax: 'ajaxRequest',
		loadingTemplate: '<i class="fa fa-spinner fa-spin fa-fw fa-2x"></i>'
	});
	
	$('#spinner').hide();
	$('table').show();
	
	$('.filter').change(function(e) {
		refresh();
	});
	
	$('#refresh').click(function(e){ 
		refresh();
	});
	
	$(document).on('click', '.deleteFile', function(e) {
		e.preventDefault();
		
		var message = '';
		if($(this).data('folder')) {
			message = "Are you sure you want to delete the folder named " + $(this).data('name') + "?<br><br><strong>WARNING:</strong> If this folder contains any content it will also be deleted.";
		} else {
			message = "Are you sure you want to delete the file named " + $(this).data('name') + "?";
		}
		var path = $(this).data('path');
		bootbox.confirm({
    		message: message,
		    buttons: {
		        confirm: {
		            label: 'Yes',
		            className: 'btn-success'
		        },
		        cancel: {
		            label: 'No',
		            className: 'btn-danger'
		        }
		    },
		    callback: function (result) {
			debugger;
		        if(result)
		        {
		        	var params = {
							path: path
					};
					$.post({
						  url: '/app/vfs/delete',
						  data: params,
						  dataType: 'json',
						  success: function(data) {
							if(data.success) {
								refresh();
								JadaptiveUtils.success($('#feedback'), data.message);
							} else {
								JadaptiveUtils.error($('#feedback'), data.message);
							}
						  }
					});
		        }
		    }
		});
		
		
	});
	
	$('#createDirectory').keydown(function(e) {
		if (e.keyCode === 13) {
   		   	e.preventDefault();
           	
			var params = { name: $('#createDirectory').val(),
						   path: $('#path').val()
			}
			$.post({
			  url: '/app/vfs/createFolder',
			  data: params,
			  dataType: 'json',
			  success: function(data) {
				if(data.success) {
					debugger;
					$('#createDirectory').val('');
					refresh();
					JadaptiveUtils.success($('#feedback'), data.message);
				} else {
					JadaptiveUtils.error($('#feedback'), data.message);
				}
			  }
			});
        }
	});
	
	changePath('');
});
