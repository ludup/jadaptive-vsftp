package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.TaskDefinition;

@ObjectDefinition(resourceKey = CreateFolderTask.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = AbstractFileTargetTask.BUNDLE)
@TaskDefinition(impl = CreateFolderTaskImpl.class, result = FileLocationResult.class, bundle = AbstractFileTargetTask.BUNDLE)
public class CreateFolderTask extends AbstractFileTargetTask {

	private static final long serialVersionUID = 7900486943823628996L;
	
	public static final String RESOURCE_KEY = "createFolder";

	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(OPTIONS_VIEW)
	Boolean errorIfExists;
	
	public Boolean getErrorIfExists() {
		return errorIfExists;
	}

	public void setErrorIfExists(Boolean errorIfExists) {
		this.errorIfExists = errorIfExists;
	}
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
