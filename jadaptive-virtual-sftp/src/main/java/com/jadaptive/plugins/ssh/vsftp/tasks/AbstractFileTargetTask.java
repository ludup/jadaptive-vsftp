package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;

public abstract class AbstractFileTargetTask extends AbstractFileTask {

	public static final String BUNDLE = "fileTasks";
	
	private static final long serialVersionUID = 1060003745642757326L;
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	@ObjectView(TARGET_VIEW)
	FileTarget target;

	public FileTarget getTarget() {
		return target;
	}

	public void setTarget(FileTarget target) {
		this.target = target;
	}
	
	
}
