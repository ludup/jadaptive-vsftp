package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;


public abstract class AbstractFileSourceTask extends AbstractFileTask {

	private static final long serialVersionUID = -1123964328628435346L;
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED, view = SOURCE_VIEW)
	LocalFileSources source;

	public LocalFileSources getSource() {
		return source;
	}

	public void setSource(LocalFileSources source) {
		this.source = source;
	}

	
}
