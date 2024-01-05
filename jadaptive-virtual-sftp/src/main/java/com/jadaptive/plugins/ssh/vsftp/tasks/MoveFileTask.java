package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.TaskDefinition;

@ObjectDefinition(resourceKey = MoveFileTask.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = AbstractFileTargetTask.BUNDLE)
@TaskDefinition(impl = MoveFileTaskImpl.class, result = FileLocationResult.class, bundle = AbstractFileTargetTask.BUNDLE)
public class MoveFileTask extends AbstractFileTansferTask {

	private static final long serialVersionUID = 1068917947192402468L;

	public static final String RESOURCE_KEY = "moveFile";

	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
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
