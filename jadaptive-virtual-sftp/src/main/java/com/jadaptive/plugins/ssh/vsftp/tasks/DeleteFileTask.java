package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(name = "Delete File", resourceKey = DeleteFileTask.RESOURCE_KEY, type = ObjectType.OBJECT)
public class DeleteFileTask extends AbstractFileTargetTask {

	private static final long serialVersionUID = -2899944412470589972L;
	
	public static final String RESOURCE_KEY = "deleteFile";

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
