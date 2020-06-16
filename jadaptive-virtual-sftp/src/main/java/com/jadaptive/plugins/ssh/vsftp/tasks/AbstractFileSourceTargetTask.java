package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.tasks.Task;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;

public class AbstractFileSourceTargetTask extends Task {

	private static final long serialVersionUID = 2893752481039355444L;

	@ObjectField(name = "Source", 
			description = "The source information for this task", 
			type = FieldType.OBJECT_EMBEDDED)
	FileTarget source;
	
	@ObjectField(name = "Target", 
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
