package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.tasks.Task;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;

@ObjectViewDefinition(value = PushFileTask.LOCAL_VIEW, weight = 0, bundle = "fileTasks")
public abstract class AbstractFileSourceTask extends Task {

	private static final long serialVersionUID = -1123964328628435346L;

	public static final String LOCAL_VIEW = "localView";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	@ObjectView(value = LOCAL_VIEW)
	FileSource source;

	public FileSource getSource() {
		return source;
	}

	public void setSource(FileSource source) {
		this.source = source;
	}

	
}
