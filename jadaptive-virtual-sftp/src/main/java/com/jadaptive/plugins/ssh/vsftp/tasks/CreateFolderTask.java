package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(name = "Create Folder", resourceKey = CreateFolderTask.RESOURCE_KEY, type = ObjectType.OBJECT)
public class CreateFolderTask extends AbstractFileTargetTask {

	private static final long serialVersionUID = 7900486943823628996L;
	
	public static final String RESOURCE_KEY = "createFolder";

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
