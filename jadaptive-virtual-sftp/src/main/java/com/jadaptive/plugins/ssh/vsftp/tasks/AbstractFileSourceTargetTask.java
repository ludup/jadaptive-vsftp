package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;

@ObjectViewDefinition(value = AbstractFileTransferTask.REMOTE_VIEW, weight = 100)
public abstract class AbstractFileSourceTargetTask extends AbstractFileSourceTask {

	private static final long serialVersionUID = 2893752481039355444L;

	public static final String REMOTE_VIEW = "remoteView";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	@ObjectView(REMOTE_VIEW)
	FileTarget target;

	public FileTarget getTarget() {
		return target;
	}

	public void setTarget(FileTarget target) {
		this.target = target;
	}
	
	
}
