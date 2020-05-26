package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(name = "Create File Result", recurse = false, resourceKey = "createFileTaskResult", type = ObjectType.OBJECT)
public class CreateFileTaskResult extends TaskResult {

	public static final String RESOURCE_KEY = "fileCreation.result";
	public static final String EVENT_NAME = "File Created";
	
	@ObjectField(name = "Location", description = "The target file system for this task", type = FieldType.ENUM)
	FileLocation location;
	
	@ObjectField(name = "File Name", description = "The name of the file created", type = FieldType.TEXT)
	String filename;

	public CreateFileTaskResult(String filename, Throwable e) {
		super(RESOURCE_KEY, e);
		this.filename = filename;
	}

	public CreateFileTaskResult(String filename) {
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
