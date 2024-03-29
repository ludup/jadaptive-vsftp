package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(recurse = false, resourceKey = "deleteFolderTaskResult", type = ObjectType.OBJECT)
public class DeleteFolderTaskResult extends FileTaskResult {

	private static final long serialVersionUID = -1139408864847443271L;
	
	public static final String RESOURCE_KEY = "folderDeletion.result";
	public static final String EVENT_NAME = "Folder Deleted";
	
	@ObjectField(type = FieldType.TEXT)
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
