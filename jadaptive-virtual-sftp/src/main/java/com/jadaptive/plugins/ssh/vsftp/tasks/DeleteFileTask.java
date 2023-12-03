package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.TaskDefinition;

@ObjectDefinition(resourceKey = DeleteFileTask.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = AbstractFileTargetTask.BUNDLE)
@TaskDefinition(impl = DeleteFileTaskImpl.class, result = FileLocationResult.class, bundle = AbstractFileTargetTask.BUNDLE)
public class DeleteFileTask extends AbstractFileTargetTask {

	private static final long serialVersionUID = -2899944412470589972L;
	
	public static final String RESOURCE_KEY = "deleteFile";

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
