package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.plugins.ssh.vsftp.events.TransferResult;

@ObjectDefinition(resourceKey = FileUploadResult.RESOURCE_KEY, type = ObjectType.OBJECT)
public class FileUploadResult extends AbstractFileTransferResult {

	private static final long serialVersionUID = 2514929934164951974L;

	public static final String RESOURCE_KEY = "fileUploadResult";
	
	
	public FileUploadResult(TransferResult result, Boolean appended, Throwable e) {
		super(RESOURCE_KEY, result, appended, e);
	}

	public FileUploadResult(TransferResult result, Boolean appended) {
		super(RESOURCE_KEY, result, appended);
	}

	@Override
	public String getEventGroup() {
		return "fileTasks";
	}
}
