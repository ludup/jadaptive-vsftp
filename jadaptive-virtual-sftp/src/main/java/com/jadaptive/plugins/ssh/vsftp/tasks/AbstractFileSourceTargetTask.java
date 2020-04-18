package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.tasks.Task;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;

public class AbstractFileSourceTargetTask extends Task {

	@Column(name = "Source", 
			description = "The source information for this task", 
			type = FieldType.OBJECT_EMBEDDED)
	FileTarget source;
	
	@Column(name = "Target", 
			description = "The target information for this task", 
			type = FieldType.OBJECT_EMBEDDED)
	FileTarget target;

	public FileTarget getSource() {
		return source;
	}

	public void setSource(FileTarget source) {
		this.source = source;
	}

	public FileTarget getTarget() {
		return target;
	}

	public void setTarget(FileTarget target) {
		this.target = target;
	}

	@Override
	public String getResourceKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
