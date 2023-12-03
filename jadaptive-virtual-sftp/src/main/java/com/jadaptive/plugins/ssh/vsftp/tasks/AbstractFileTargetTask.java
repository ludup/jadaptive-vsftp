package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.tasks.Task;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;

public abstract class AbstractFileTargetTask extends Task {

	public static final String BUNDLE = "fileTasks";
	
	private static final long serialVersionUID = 1060003745642757326L;
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	FileTarget target;

	public FileTarget getTarget() {
		return target;
	}

	public void setTarget(FileTarget target) {
		this.target = target;
	}
	
	
}
