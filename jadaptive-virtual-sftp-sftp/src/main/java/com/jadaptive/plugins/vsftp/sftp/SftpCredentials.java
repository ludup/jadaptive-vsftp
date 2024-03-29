package com.jadaptive.plugins.vsftp.sftp;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.schemes.BasicCredentials;

@ObjectDefinition(resourceKey = SftpCredentials.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = SftpFolder.RESOURCE_KEY)
public class SftpCredentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = 3244595514117790021L;

	public static final String RESOURCE_KEY = "sftpCredentials";

	@ObjectField(type = FieldType.OBJECT_EMBEDDED )
	BasicCredentials basicCredentials;
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	PrivateKeyCredentials privateKeyCredentials;

	public SftpCredentials() { }
	
	public BasicCredentials getBasicCredentials() {
		return basicCredentials;
	}

	public void setBasicCredentials(BasicCredentials basicCredentials) {
		this.basicCredentials = basicCredentials;
	}

	public PrivateKeyCredentials getPrivateKeyCredentials() {
		return privateKeyCredentials;
	}

	public void setPrivateKeyCredentials(PrivateKeyCredentials privateKeyCredentials) {
		this.privateKeyCredentials = privateKeyCredentials;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
