package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;

@Template(name = "File Source", resourceKey = FileSource.RESOURCE_KEY, type = EntityType.OBJECT)
public class FileSource extends AbstractUUIDEntity {

	public static final String RESOURCE_KEY = "fileSource";

	@Column(name="Location", description = "The source location", type = FieldType.ENUM)
	FileLocation location;
	
	@Column(name="File", description = "The filename on the source location", type = FieldType.TEXT)
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
	
	
}
