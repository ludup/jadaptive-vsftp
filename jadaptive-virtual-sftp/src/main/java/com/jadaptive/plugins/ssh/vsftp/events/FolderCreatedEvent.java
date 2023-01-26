package com.jadaptive.plugins.ssh.vsftp.events;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.events.AuditedObject;
import com.jadaptive.api.events.ObjectEvent;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@AuditedObject
@ObjectDefinition(resourceKey = FolderCreatedEvent.RESOURCE_KEY, scope = ObjectScope.GLOBAL, 
		type = ObjectType.OBJECT, bundle = VirtualFolder.RESOURCE_KEY,
		creatable = false, updatable = false, deletable = false)
@ObjectViews({@ObjectViewDefinition(bundle = VirtualFolder.RESOURCE_KEY, value = ObjectEvent.OBJECT_VIEW)})
public class FolderCreatedEvent extends FileOperationEvent {

	private static final long serialVersionUID = -2044630063808224880L;

	public static final String RESOURCE_KEY = "folderCreatedEvent";
	
	public FolderCreatedEvent(FileOperation result) {
		super(result, RESOURCE_KEY);
	}
	
	public FolderCreatedEvent(FileOperation result, Throwable e) {
		super(result, RESOURCE_KEY, e);
	}
}
