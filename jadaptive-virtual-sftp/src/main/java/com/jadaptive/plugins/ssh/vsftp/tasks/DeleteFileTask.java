package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.template.Template;

@Template(name = "Delete File", resourceKey = DeleteFileTask.RESOURCE_KEY, type = EntityType.OBJECT)
public class DeleteFileTask extends AbstractFileTargetTask {

	public static final String RESOURCE_KEY = "deleteFile";

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
