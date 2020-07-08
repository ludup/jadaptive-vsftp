package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(recurse = false, resourceKey = "deleteFileTaskResult", type = ObjectType.OBJECT)
public class DeleteFileTaskResult extends TaskResult {

	private static final long serialVersionUID = 96293955438850683L;
	
	public static final String RESOURCE_KEY = "fileDeletion.result";
	public static final String EVENT_NAME = "File Deleted";
	
	@ObjectField(type = FieldType.ENUM)
	FileLocation location;
	
	@ObjectField(type = FieldType.TEXT)
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
