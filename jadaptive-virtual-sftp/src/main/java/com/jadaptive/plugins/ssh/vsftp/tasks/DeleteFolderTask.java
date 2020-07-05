package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(resourceKey = DeleteFolderTask.RESOURCE_KEY, type = ObjectType.OBJECT)
public class DeleteFolderTask extends AbstractFileTargetTask {

	private static final long serialVersionUID = 1700728212983579995L;

	public static final String RESOURCE_KEY = "deleteFolder";
	
	@ObjectField(name = "Delete Contents", 
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
