package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.tasks.TaskResult;

public class FileTaskResult extends TaskResult {

	private static final long serialVersionUID = -2647802923985077711L;

	public FileTaskResult(String resourceKey, Throwable e) {
		super(resourceKey, e);
	}

	public FileTaskResult(String resourceKey) {
		super(resourceKey);
	}

	@Override
	public String getEventGroup() {
		return "fileTasks";
	}

}
