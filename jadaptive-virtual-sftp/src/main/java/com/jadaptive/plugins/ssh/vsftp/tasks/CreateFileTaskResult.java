package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;

@Template(name = "Create File Result", resourceKey = "createFileTaskResult", type = EntityType.OBJECT)
public class CreateFileTaskResult extends TaskResult {

	public static final String RESOURCE_KEY = "fileCreation.result";
	public static final String EVENT_NAME = "File Created";
	
	@Column(name = "Location", description = "The target file system for this task", type = FieldType.ENUM)
	FileLocation location;
	
	@Column(name = "File Name", description = "The name of the file created", type = FieldType.TEXT)
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
