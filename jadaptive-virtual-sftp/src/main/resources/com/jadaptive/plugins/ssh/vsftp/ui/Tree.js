
const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

function renderPerms(val, obj) {
	var perms = obj.readable ? 'r' : '-';
	perms += (!obj.readOnly && obj.writable) ? 'w' : '-';
	return perms;
}

function renderName(val, obj) {
	var icon = (obj.directory ? (obj.encrypted ? '<i class="far fa-folder"></i>' : '<i class="far fa-folder"></i>')
		 :  (obj.encrypted ? '<i class="far fa-file-lock"></i>' : '<i class="far fa-file"></i>'));
	if (obj.directory) {
		return '<a class="clickPath" href="' + obj.path + '">' + icon + ' ' + obj.name + '</a>';
	} else {
		return icon + ' ' + obj.name;
	}
}

function renderLength(val) {
	if (val > 0) {
		var i = Math.floor(Math.log(val) / Math.log(1000));
		return (val / Math.pow(1000, i)).toFixed(2) * 1 + ' ' + ['B', 'KB', 'MB', 'GB', 'TB'][i];
	}

	return '';
}

function renderDate(val) {

	if (!val) {
		return '';
	}

	var d = new Date(val);
	
	return months[d.getMonth()] + ' ' + d.getDate() + ', ' + d.getFullYear() + ' ' + d.getHours() + ':' + d.getMinutes();

}

function renderShare(obj) {

		var html = '<span class="dropdown"><a class="createLink me-2 dropdown-toggle" id="' + obj.id + '" role="button" data-bs-toggle="dropdown" aria-expanded="false" href="#"><i class="far fa-link fa-fw"></i></a>';
		html += '<ul class="dropdown-menu" aria-labelledby="' + obj.id + '" style="z-index: 999999;">';	
	
		if(obj.shareFiles && !obj.directory) {
			html += '<li><a class="dropdown-item copyLink" href="#" data-path="' + obj.path + '"><i class="far fa-copy fa-fw me-1"></i> Share Link</a></li>';
			html += '<li><a class="dropdown-item createShare" href="#" data-path="' + obj.path + '"><i class="far fa-share-alt fa-fw me-1"></i> Share with Options</a></li>';
		}
		
		if(obj.shareFolders && obj.directory) {
			html += '<li><a class="dropdown-item copyLink" href="#" data-path="' + obj.path + '"><i class="far fa-copy fa-fw me-1"></i> Share Link</a></li>';
			html += '<li><a class="dropdown-item createShare" href="#" data-path="' + obj.path + '"><i class="far fa-share-alt fa-fw me-1"></i> Share with Options</a></li>';
		}

		if(obj.shareFiles || obj.shareFolders) {
			html += '</ul></span>';
		}
		
		return html;
	
}
function renderActions(val, obj) {
	var html = '';
	
	if(obj.mount) {
		html += '<a class="me-2" href="/app/ui/mount-report/' + obj.mountUuid + '"><i class="far fa-chart-pie fa-fw"></i></a>';
	} else {
		html += '<i class="far fa-fw"></i>';
	}
	if(!obj.mount && !obj.readOnly) {
		html += '<a class="deleteFile me-2" href="#" data-name="' + obj.name + '" data-folder="' + obj.directory + '" data-path="' + obj.path + '"><i class="far fa-trash fa-fw"></i></a>';
	} else {
		html += '<i class="far fa-fw"></i>';
	}
	
	if (!obj.directory) {
		html += '<a class="downloadFile me-2" href="/app/vfs/downloadFile' + obj.path + '"><i class="far fa-download fa-fw"></i></a>';
	} else {
		html += '<i class="far fa-fw"></i>';	
	}
	
	if(obj.shareFiles && !obj.directory) {
		html += renderShare(obj);
	} else if(obj.shareFolders && obj.directory) {
		renderShare(obj);
	} else {
		html += '<i class="far fa-fw"></i>';
	}

	return html;
}

function getMaximumFiles() {
	var results = $('#maximumFiles').val();
	if (results.trim() === '') {
		return 1000;
	}
	return results;
}

function getBoolean(val) {
	if (val === 'true') {
		return true;
	}
	return false;
}

function getPath() {

	var path = $('#path').val();
	if (!path) {
		path = window.location.pathname.replace('/app/ui/tree', '');
		if (path === '') {
			path = '/';
		}
	}
	return path;
}

function ajaxRequest(params) {

	$('#feedback').empty();

	var path = getPath();

	$.get('/app/vfs/stat' + path).then(function(res) {
		if (res.success) {
			$('#uploadFiles').attr('href', '/app/ui/upload-files' + path);
			if(!res.resource.readOnly && res.resource.writable) {
				$('.writeActions').show();
			} else {
				$('.writeActions').hide();
			}
			var url = '/app/vfs/listDirectory' + path;
		
			params.data.filter = $('#filter').val();
			params.data.files = $('#files').is(":checked");
			params.data.folders = $('#folders').is(":checked");
			params.data.hidden = $('#hidden').is(":checked");
			params.data.maximumResults = getMaximumFiles();
			params.data.searchDepth = $('#searchDepth').val();
		
			$.get(url + '?' + $.param(params.data)).then(function(res) {
		
				$('table').show();
		
				if (res.success) {
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
		} else {
			JadaptiveUtils.error($('#feedback'), res.message);
		}
	});
}

function changePath(path) {

	$('#path').val(path);
	$('table').bootstrapTable('refresh');
}

function updateBreadcrumb(path) {
	path = decodeURI(path);
	$('#breadcrumb').empty();
	$('#breadcrumb').append('<li class="breadcrumb-item"><a class="clickPath" href="/"><i class="far fa-hdd"></i></a></li>');
	var lastIdx = path.indexOf('/');

	while (lastIdx < path.length - 1) {

		var idx = path.indexOf('/', lastIdx + 1);
		if (idx == -1) {
			idx = path.length;
		}
		$('#breadcrumb').append('<li class="breadcrumb-item"><a class="clickPath" href="'
			+ path.substring(0, idx) + '">' + path.substring(lastIdx + 1, idx) + '</a></li>');
		lastIdx = idx;
	}
	$('.breadcrumb-item').last().addClass('active');

}

function refresh() {
	var path = getPath();
	if (!path) {
		path = window.location.pathname.replace('/app/ui/tree', '');
		if (path === '') {
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

	$('#refresh').click(function(e) {
		refresh();
	});


	$(document).on('click', '.copyLink', function(e) {
		e.preventDefault();

		$.get({
			url: '/app/vfs/share/public' + $(this).data('path'),
			dataType: 'json',
			success: function(data) {
				if (data.success) {
					var link = window.location.origin + '/app/ui/share/' + data.resource.shortCode;
					navigator.clipboard.writeText(link);
					JadaptiveUtils.success($('#feedback'), "The public link has been copied to the clipboard.");
				} else {
					JadaptiveUtils.error($('#feedback'), data.message);
				}
			}
		});
	});
	
	$(document).on('click', '.createShare', function(e) {
		e.preventDefault();

		$.get({
			url: '/app/vfs/share/create' + $(this).data('path'),
			dataType: 'json',
			success: function(data) {
				if (data.success) {
					window.location = '/app/ui/create/sharedFiles';
				} else {
					JadaptiveUtils.error($('#feedback'), data.message);
				}
			}
		});
	});
	
	$(document).on('click', '.deleteFile', function(e) {
		e.preventDefault();

		var message = '';
		if ($(this).data('folder')) {
			message = "Are you sure you want to delete the folder named " + $(this).data('name') + "?<br><br><strong>WARNING:</strong> If this folder contains any content it will also be deleted.";
		} else {
			message = "Are you sure you want to delete the file named " + $(this).data('name') + "?";
		}
		var path = $(this).data('path');
		bootbox.confirm({
			message: message,
			closeButton: false,
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
			callback: function(result) {

				if (result) {
					var params = {
						path: path
					};
					$.post({
						url: '/app/vfs/delete',
						data: params,
						dataType: 'json',
						success: function(data) {
							if (data.success) {
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

			var params = {
				name: $('#createDirectory').val(),
				path: $('#path').val()
			}
			$.post({
				url: '/app/vfs/createFolder',
				data: params,
				dataType: 'json',
				success: function(data) {
					if (data.success) {

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

	//changePath('');
});
