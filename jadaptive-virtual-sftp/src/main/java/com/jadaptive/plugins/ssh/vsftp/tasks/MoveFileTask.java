package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.TaskDefinition;
import com.jadaptive.api.templates.ObjectDynamicField;

@ObjectDefinition(resourceKey = MoveFileTask.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = AbstractFileTargetTask.BUNDLE)
@TaskDefinition(impl = MoveFileTaskImpl.class, result = SourceLocationResult.class, bundle = AbstractFileTargetTask.BUNDLE)
@ObjectDynamicField(field = "source.paths", dependsOn = "source.location", dependsValue = "!UPLOAD_FORM")
public class MoveFileTask extends AbstractFileTransferTask {

	private static final long serialVersionUID = 1068917947192402468L;

	public static final String RESOURCE_KEY = "moveFile";

	@ObjectField(type = FieldType.BOOL, defaultValue = "false", view = AbstractFileTransferTask.OPTIONS_VIEW)
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
