package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.Template;

@Template(name = "Create Folder", resourceKey = CreateFolderTask.RESOURCE_KEY, type = ObjectType.OBJECT)
public class CreateFolderTask extends AbstractFileTargetTask {

	public static final String RESOURCE_KEY = "createFolder";

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
