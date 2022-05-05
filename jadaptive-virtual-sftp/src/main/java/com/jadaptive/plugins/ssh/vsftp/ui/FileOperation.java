package com.jadaptive.plugins.ssh.vsftp.ui;

import java.util.Date;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.events.ObjectEvent;
import com.jadaptive.api.repository.UUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@ObjectDefinition(resourceKey = FileOperation.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.COLLECTION, bundle = VirtualFolder.RESOURCE_KEY)
public class FileOperation extends UUIDEntity {

	private static final long serialVersionUID = 7395109201689299682L;

	public static final String RESOURCE_KEY = "fileOperation";
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = ObjectEvent.OBJECT_VIEW, bundle = VirtualFolder.RESOURCE_KEY)
	String filename;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = ObjectEvent.OBJECT_VIEW, bundle = VirtualFolder.RESOURCE_KEY)
	String path;
	
	@ObjectField(type = FieldType.TIMESTAMP)
	@ObjectView(value = ObjectEvent.OBJECT_VIEW, bundle = VirtualFolder.RESOURCE_KEY)
	Date started;
	
	@ObjectField(type = FieldType.TIMESTAMP)
	@ObjectView(value = ObjectEvent.OBJECT_VIEW, bundle = VirtualFolder.RESOURCE_KEY)
	Date ended;
	
	public FileOperation(String filename, String path, Date started) {
		this(filename, path, started, started);
	}
	
	public FileOperation(String filename, String path, Date started, Date ended) {
		
		this.filename = filename;
		this.path = path;
		this.started = started;
		this.ended = ended;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public String getFilename() {
		return filename;
	}

	public String getPath() {
		return path;
	}

	public Date getStarted() {
		return started;
	}

	public Date getEnded() {
		return ended;
	}

	
}
