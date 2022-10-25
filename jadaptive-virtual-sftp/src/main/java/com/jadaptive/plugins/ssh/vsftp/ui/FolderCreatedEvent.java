package com.jadaptive.plugins.ssh.vsftp.ui;

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
@ObjectDefinition(resourceKey = FolderCreatedEvent.RESOURCE_KEY, scope = ObjectScope.GLOBAL, 
		type = ObjectType.OBJECT, bundle = VirtualFolder.RESOURCE_KEY,
		creatable = false, updatable = false, deletable = false)
@ObjectViews({@ObjectViewDefinition(bundle = VirtualFolder.RESOURCE_KEY, value = ObjectEvent.OBJECT_VIEW)})
public class FolderCreatedEvent extends ObjectEvent<FileOperation> {

	private static final long serialVersionUID = -2044630063808224880L;

	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	@ObjectView(value = ObjectEvent.OBJECT_VIEW, bundle = VirtualFolder.RESOURCE_KEY)
	@Validator(type = ValidationType.RESOURCE_KEY, value = TransferResult.RESOURCE_KEY)
	FileOperation object;
	
	public static final String RESOURCE_KEY = "folderCreatedEvent";
	
	public FolderCreatedEvent(FileOperation result) {
		super(RESOURCE_KEY, "files");
		this.object = result;
	}
	
	public FolderCreatedEvent(FileOperation result, Throwable e) {
		super(RESOURCE_KEY, "files", e);
		this.object = result;
	}

	@Override
	public FileOperation getObject() {
		return object;
	}

}
