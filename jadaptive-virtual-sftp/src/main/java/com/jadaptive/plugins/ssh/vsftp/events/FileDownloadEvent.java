package com.jadaptive.plugins.ssh.vsftp.events;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.events.AuditedObject;
import com.jadaptive.api.events.ObjectEvent;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@AuditedObject
@ObjectDefinition(resourceKey = FileDownloadEvent.RESOURCE_KEY, scope = ObjectScope.GLOBAL,
		type = ObjectType.OBJECT, bundle = VirtualFolder.RESOURCE_KEY,
			creatable = false, updatable = false, deletable = false)
@ObjectViews({@ObjectViewDefinition(bundle = VirtualFolder.RESOURCE_KEY, value = ObjectEvent.OBJECT_VIEW)})
public class FileDownloadEvent extends TransferResultEvent {

	private static final long serialVersionUID = -2044630063808224880L;

	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	@ObjectView(value = ObjectEvent.OBJECT_VIEW, bundle = VirtualFolder.RESOURCE_KEY)
	@Validator(type = ValidationType.RESOURCE_KEY, value = TransferResult.RESOURCE_KEY)
	TransferResult object;
	
	public FileDownloadEvent() { }
	
	public static final String RESOURCE_KEY = "fileDownloadEvent";
	public FileDownloadEvent(TransferResult result) {
		super(result, RESOURCE_KEY);
	}
	
	public FileDownloadEvent(TransferResult result, Throwable e) {
		super(result, RESOURCE_KEY, e);
	}
}
