package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;

@ObjectDefinition(resourceKey = FileLocationResult.RESOURCE_KEY, type = ObjectType.OBJECT)
public class FileLocationResult extends TaskResult {

	private static final long serialVersionUID = -2647802923985077711L;

	public static final String RESOURCE_KEY = "fileLocationResult";
	
	@ObjectField(type = FieldType.ENUM)
	FileLocation location;
	
	@ObjectField(type = FieldType.TEXT)
	String filename;
	
	public FileLocationResult(FileLocation location, String filename, Throwable e) {
		super(RESOURCE_KEY, e);
		this.location = location;
		this.filename = filename;
	}

	public FileLocationResult(FileLocation location, String filename) {
		super(RESOURCE_KEY);
		this.location = location;
		this.filename = filename;
	}

	@Override
	public String getEventGroup() {
		return "fileTasks";
	}

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
	
	

}
