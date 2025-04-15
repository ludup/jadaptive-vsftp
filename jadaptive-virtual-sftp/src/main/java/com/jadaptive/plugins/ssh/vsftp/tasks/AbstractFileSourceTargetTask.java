package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;

public abstract class AbstractFileSourceTargetTask extends AbstractFileSourceTask {

	private static final long serialVersionUID = 2893752481039355444L;

	@ObjectField(type = FieldType.OBJECT_EMBEDDED, view = TARGET_VIEW)
	FileTarget target;

	public FileTarget getTarget() {
		return target;
	}

	public void setTarget(FileTarget target) {
		this.target = target;
	}
	
	
}
