package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(recurse = false, resourceKey = "createFolderTaskResult", type = ObjectType.OBJECT)
public class CreateFolderTaskResult extends TaskResult {

	private static final long serialVersionUID = 7469143834170431561L;
	
	public static final String RESOURCE_KEY = "folderCreation.result";
	public static final String EVENT_NAME = "Folder Created";
	
	@ObjectField(name = "Folder Name", description = "The name of the folder created", type = FieldType.TEXT)
	String filename;

	public CreateFolderTaskResult(String filename, Throwable e) {
		super(RESOURCE_KEY, e);
		this.filename = filename;
	}

	public CreateFolderTaskResult(String filename) {
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
