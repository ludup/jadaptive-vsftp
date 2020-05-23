package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;

@Template(name = "Delete Folder", resourceKey = DeleteFolderTask.RESOURCE_KEY, type = ObjectType.OBJECT)
public class DeleteFolderTask extends AbstractFileTargetTask {

	public static final String RESOURCE_KEY = "deleteFolder";
	
	@Column(name = "Delete Contents", 
			description = "Delete the folder and its contents",
			type = FieldType.BOOL)
	Boolean deleteContents;

	public Boolean getDeleteContents() {
		return deleteContents;
	}

	public void setDeleteContents(Boolean deleteContents) {
		this.deleteContents = deleteContents;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
}
