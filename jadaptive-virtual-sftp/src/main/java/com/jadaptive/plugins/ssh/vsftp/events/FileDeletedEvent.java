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
@ObjectDefinition(resourceKey = FileDeletedEvent.RESOURCE_KEY, scope = ObjectScope.GLOBAL, 
	type = ObjectType.OBJECT, bundle = VirtualFolder.RESOURCE_KEY,
	creatable = false, updatable = false, deletable = false)
@ObjectViews({@ObjectViewDefinition(bundle = VirtualFolder.RESOURCE_KEY, value = ObjectEvent.OBJECT_VIEW)})
public class FileDeletedEvent extends FileOperationEvent {

	private static final long serialVersionUID = -4051528433333811257L;

	public static final String RESOURCE_KEY = "fileDeletedEvent";
	
	public FileDeletedEvent() { }
	
	public FileDeletedEvent(FileOperation result) {
		super(result, RESOURCE_KEY);
	}
	
	public FileDeletedEvent(FileOperation result, Throwable e) {
		super(result, RESOURCE_KEY, e);
	}

}
