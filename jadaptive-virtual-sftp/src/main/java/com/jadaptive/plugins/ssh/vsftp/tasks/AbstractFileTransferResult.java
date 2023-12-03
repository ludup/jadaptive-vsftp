package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.plugins.ssh.vsftp.events.TransferResult;

public abstract class AbstractFileTransferResult extends TaskResult {

	private static final long serialVersionUID = -3228542037269240264L;

	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	@Validator(type = ValidationType.RESOURCE_KEY, value = TransferResult.RESOURCE_KEY)
	TransferResult result;
	
	@ObjectField(type = FieldType.BOOL)
	Boolean appended;
	
	public AbstractFileTransferResult(String resourceKey, TransferResult result, Boolean appended, Throwable e) {
		super(resourceKey, e);
		this.result = result;
		this.appended = appended;
	}

	public AbstractFileTransferResult(String resourceKey, TransferResult result, Boolean appended) {
		super(resourceKey);
		this.result = result;
		this.appended = appended;
	}

	@Override
	public String getEventGroup() {
		return "fileTasks";
	}

	public TransferResult getResult() {
		return result;
	}

	public void setResult(TransferResult result) {
		this.result = result;
	}

	public Boolean getAppended() {
		return appended;
	}

	public void setAppended(Boolean appended) {
		this.appended = appended;
	}
	
}
