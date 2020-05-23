package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;

@Template(name = "Delete Folder Result", recurse = false, resourceKey = "deleteFolderTaskResult", type = ObjectType.OBJECT)
public class DeleteFolderTaskResult extends TaskResult {

	public static final String RESOURCE_KEY = "folderDeletion.result";
	public static final String EVENT_NAME = "Folder Deleted";
	
	@Column(name = "Folder Name", description = "The name of the folder deleted", type = FieldType.TEXT)
	String filename;

	public DeleteFolderTaskResult(String filename, Throwable e) {
		super(RESOURCE_KEY, e);
		this.filename = filename;
	}

	public DeleteFolderTaskResult(String filename) {
		super(RESOURCE_KEY);
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
