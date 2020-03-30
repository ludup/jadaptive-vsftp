package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;

@Template(name = "Create Folder Result", resourceKey = "createFolderTaskResult", type = EntityType.OBJECT)
public class CreateFolderTaskResult extends TaskResult {

	public static final String RESOURCE_KEY = "folderCreation.result";
	public static final String EVENT_NAME = "Folder Created";
	
	@Column(name = "Folder Name", description = "The name of the folder created", type = FieldType.TEXT)
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
