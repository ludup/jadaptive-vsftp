package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;

@ObjectDefinition(resourceKey = FileTargetErrorResult.RESOURCE_KEY, type = ObjectType.OBJECT)
public class FileTargetErrorResult extends TaskResult {

	private static final long serialVersionUID = 2514929934164951974L;

	public static final String RESOURCE_KEY = "fileTargetError";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	FileTarget target;
	
	public FileTargetErrorResult(FileTarget target, Throwable e) {
		super(RESOURCE_KEY, e);
	}
	
	@Override
	public String getEventGroup() {
		return "fileTasks";
	}

	public FileTarget getTarget() {
		return target;
	}

	public void setTarget(FileTarget target) {
		this.target = target;
	}
}
