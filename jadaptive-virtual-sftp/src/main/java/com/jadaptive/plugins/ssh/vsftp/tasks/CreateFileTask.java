package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(resourceKey = CreateFileTask.RESOURCE_KEY, type = ObjectType.OBJECT)
public class CreateFileTask extends AbstractFileTargetTask {

	private static final long serialVersionUID = -3844905236345385176L;
	
	public static final String RESOURCE_KEY = "createFile";

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
