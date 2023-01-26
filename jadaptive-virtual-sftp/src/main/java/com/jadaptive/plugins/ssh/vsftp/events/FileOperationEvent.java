package com.jadaptive.plugins.ssh.vsftp.events;

import com.jadaptive.api.events.ObjectEvent;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

public class FileOperationEvent  extends ObjectEvent<FileOperation> {

	private static final long serialVersionUID = -2044630063808224880L;

	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	@ObjectView(value = ObjectEvent.OBJECT_VIEW, bundle = VirtualFolder.RESOURCE_KEY)
	@Validator(type = ValidationType.RESOURCE_KEY, value = FileOperation.RESOURCE_KEY)
	FileOperation object;

	public FileOperationEvent(FileOperation result, String resourceKey) {
		super(resourceKey, "files");
		this.object = result;
		setEventDescription(result.getFilename());
	}
	
	public FileOperationEvent(FileOperation result, String resourceKey, Throwable e) {
		super(resourceKey, "files", e);
		this.object = result;
		setEventDescription(result.getFilename());
	}

	@Override
	public FileOperation getObject() {
		return object;
	}
}
