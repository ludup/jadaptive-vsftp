package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.TaskDefinition;

@ObjectDefinition(resourceKey = DeleteFolderTask.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = AbstractFileTargetTask.BUNDLE)
@TaskDefinition(impl = DeleteFolderTaskImpl.class, result = FileLocationResult.class, bundle = AbstractFileTargetTask.BUNDLE)
public class DeleteFolderTask extends AbstractFileTargetTask {

	private static final long serialVersionUID = 1700728212983579995L;

	public static final String RESOURCE_KEY = "deleteFolder";
	
	@ObjectField(type = FieldType.BOOL)
	@ObjectView(OPTIONS_VIEW)
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
