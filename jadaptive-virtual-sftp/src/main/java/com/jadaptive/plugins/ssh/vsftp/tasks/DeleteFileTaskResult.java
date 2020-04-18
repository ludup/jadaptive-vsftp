package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;

@Template(name = "Delete File Result", recurse = false, resourceKey = "deleteFileTaskResult", type = EntityType.OBJECT)
public class DeleteFileTaskResult extends TaskResult {

	public static final String RESOURCE_KEY = "fileDeletion.result";
	public static final String EVENT_NAME = "File Deleted";
	
	@Column(name = "Location", description = "The target file system for this task", type = FieldType.ENUM)
	FileLocation location;
	
	@Column(name = "File Name", description = "The name of the file deleted", type = FieldType.TEXT)
	String filename;

	public DeleteFileTaskResult(String filename, Throwable e) {
		super(RESOURCE_KEY, e);
		this.filename = filename;
	}

	public DeleteFileTaskResult(String filename) {
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
