package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(resourceKey = FileSource.RESOURCE_KEY, type = ObjectType.OBJECT)
public class FileSource extends AbstractUUIDEntity {

	private static final long serialVersionUID = 4479279858955794444L;

	public static final String RESOURCE_KEY = "fileSource";

	@ObjectField(type = FieldType.ENUM)
	FileLocation location;
	
	@ObjectField(type = FieldType.TEXT)
	String filename;

	public FileLocation getLocation() {
		return location;
	}

	public void setLocation(FileLocation location) {
		this.location = location;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public String[] getFilePaths() {
		return filename.split(",");
	}

	
}
