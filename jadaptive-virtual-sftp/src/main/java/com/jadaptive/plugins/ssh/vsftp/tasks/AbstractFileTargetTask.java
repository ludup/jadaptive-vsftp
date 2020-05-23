package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.tasks.Task;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;

public abstract class AbstractFileTargetTask extends Task {

	@ObjectField(name = "Target", 
			description = "The target information for this task", 
			type = FieldType.OBJECT_EMBEDDED)
	FileTarget target;

	public FileTarget getTarget() {
		return target;
	}

	public void setTarget(FileTarget target) {
		this.target = target;
	}
	
	
}
