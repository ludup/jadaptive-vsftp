package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(name = "Delete File", resourceKey = DeleteFileTask.RESOURCE_KEY, type = ObjectType.OBJECT)
public class DeleteFileTask extends AbstractFileTargetTask {

	public static final String RESOURCE_KEY = "deleteFile";

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
