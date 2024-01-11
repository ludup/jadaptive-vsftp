package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;


public abstract class AbstractFileSourceTask extends AbstractFileTask {

	private static final long serialVersionUID = -1123964328628435346L;
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	@ObjectView(value = SOURCE_VIEW)
	LocalFileSources source;

	public LocalFileSources getSource() {
		return source;
	}

	public void setSource(LocalFileSources source) {
		this.source = source;
	}

	
}
