package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.tasks.Task;
import com.jadaptive.api.template.ObjectViewDefinition;

@ObjectViewDefinition(value = PushFileTask.SOURCE_VIEW, weight = 0, bundle = "fileTasks")
@ObjectViewDefinition(value = PushFileTask.TARGET_VIEW, weight = 1000, bundle = "fileTasks")
@ObjectViewDefinition(value = PushFileTask.OPTIONS_VIEW, weight = 9999999, bundle = "fileTasks")
public abstract class AbstractFileTask extends Task {

	private static final long serialVersionUID = 1063385116809679770L;
	public static final String SOURCE_VIEW = "sourceView";
	public static final String TARGET_VIEW = "targetView";
	public static final String OPTIONS_VIEW = "optionsView";
	
}
