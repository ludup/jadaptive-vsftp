package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.TaskDefinition;

@ObjectDefinition(resourceKey = PutFileTask.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = AbstractFileTargetTask.BUNDLE)
@TaskDefinition(impl = PutFileTaskImpl.class, result = FileTransferResult.class, bundle = AbstractFileTargetTask.BUNDLE)
public class PutFileTask extends AbstractFileSourceTask {

	private static final long serialVersionUID = 1068917947192402468L;

	public static final String RESOURCE_KEY = "putFile";
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(OPTIONS_VIEW)
	Boolean errorIfExists;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(TARGET_VIEW)
	String remoteDirectory;
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	SshConnectionProperties connection;

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
