package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.Template;

@Template(name = "Create File", resourceKey = CreateFileTask.RESOURCE_KEY, type = ObjectType.OBJECT)
public class CreateFileTask extends AbstractFileTargetTask {

	public static final String RESOURCE_KEY = "createFile";

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
