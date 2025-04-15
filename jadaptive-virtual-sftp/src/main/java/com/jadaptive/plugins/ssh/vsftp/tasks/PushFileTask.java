package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.TaskDefinition;

@ObjectDefinition(resourceKey = PushFileTask.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = AbstractFileTargetTask.BUNDLE)
@TaskDefinition(impl = PushFileTaskImpl.class, result = FileTransferResult.class, bundle = AbstractFileTargetTask.BUNDLE)
public class PushFileTask extends AbstractFileSourceTask {

	private static final long serialVersionUID = 1068917947192402468L;

	public static final String RESOURCE_KEY = "pushFile";
	
	@ObjectField(type = FieldType.INTEGER, defaultValue = "3", view = OPTIONS_VIEW)
	Integer chunks;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false", view = OPTIONS_VIEW)
	Boolean errorIfExists;
	
	@ObjectField(type = FieldType.TEXT, view = TARGET_VIEW)
	String remoteDirectory;
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	SshConnectionProperties connection;
	
	public Integer getChunks() {
		return chunks;
	}

	public void setChunks(Integer chunks) {
		this.chunks = chunks;
	}

	public Boolean getErrorIfExists() {
		return errorIfExists;
	}

	public void setErrorIfExists(Boolean errorIfExists) {
		this.errorIfExists = errorIfExists;
	}

	public SshConnectionProperties getConnection() {
		return connection;
	}

	public void setConnection(SshConnectionProperties connection) {
		this.connection = connection;
	}

	public String getRemoteDirectory() {
		return remoteDirectory;
	}

	public void setRemoteDirectory(String remoteDirectory) {
		this.remoteDirectory = remoteDirectory;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
