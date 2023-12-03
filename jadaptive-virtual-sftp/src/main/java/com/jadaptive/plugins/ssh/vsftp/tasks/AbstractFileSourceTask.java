package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.tasks.Task;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;

public abstract class AbstractFileSourceTask extends Task {

	private static final long serialVersionUID = -1123964328628435346L;

	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	FileTarget source;

	public FileTarget getSource() {
		return source;
	}

	public void setSource(FileTarget source) {
		this.source = source;
	}

	
}
