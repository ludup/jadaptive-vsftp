package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;

@ObjectDefinition(resourceKey = FileConnectionErrorResult.RESOURCE_KEY, type = ObjectType.OBJECT)
public class FileConnectionErrorResult extends TaskResult {

	private static final long serialVersionUID = 2514929934164951974L;

	public static final String RESOURCE_KEY = "fileConnectionError";
	
	@ObjectField(type = FieldType.TEXT)
	String connection;
	
	public FileConnectionErrorResult(String connection, Throwable e) {
		super(RESOURCE_KEY, e);
		this.connection = connection;
	}
	
	@Override
	public String getEventGroup() {
		return "fileTasks";
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}
}
