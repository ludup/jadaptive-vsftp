package com.jadaptive.plugins.ssh.vsftp.ui;

import java.util.Date;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.events.ObjectEvent;
import com.jadaptive.api.repository.UUIDEntity;
import com.jadaptive.api.template.FieldRenderer;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@ObjectDefinition(resourceKey = TransferResult.RESOURCE_KEY, scope = ObjectScope.GLOBAL, 
			type = ObjectType.OBJECT, bundle = VirtualFolder.RESOURCE_KEY,
			creatable = false, updatable = false, deletable = false)
public class TransferResult extends UUIDEntity {

	private static final long serialVersionUID = 7395109201689299682L;

	public static final String RESOURCE_KEY = "transferResult";
	
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
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = ObjectEvent.OBJECT_VIEW, bundle = VirtualFolder.RESOURCE_KEY, renderer = FieldRenderer.OPTIONAL)
	String contentHash;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = ObjectEvent.OBJECT_VIEW, bundle = VirtualFolder.RESOURCE_KEY, renderer = FieldRenderer.OPTIONAL)
	String humanHash;
	
	@ObjectField(type = FieldType.LONG)
	@ObjectView(value = ObjectEvent.OBJECT_VIEW, bundle = VirtualFolder.RESOURCE_KEY)
	long size;
	
	public TransferResult(String filename, String path, long size, Date started, Date ended, String contentHash, String humanHash) {
		this(filename, path, size, started, ended);
		this.contentHash = contentHash;
		this.humanHash = humanHash;
	}
	
	public TransferResult(String filename, String path, long size, Date started, Date ended) {
		
		this.filename = filename;
		this.path = path;
		this.size = size;
		this.started = started;
		this.ended = ended;
		this.size = size;
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

	public String getContentHash() {
		return contentHash;
	}
	
	public String getHumanHash() {
		return humanHash;
	}

	public long getSize() {
		return size;
	}

	
}
