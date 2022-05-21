package com.jadaptive.plugins.vsftp.sftp;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@ObjectDefinition(resourceKey = PrivateKeyCredentials.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = SftpFolder.RESOURCE_KEY)
public class PrivateKeyCredentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = 2797187386964915392L;

	public static final String RESOURCE_KEY = "privateKeyCredentials";

	@ObjectField(type = FieldType.TEXT_AREA)
	@ObjectView(value = VirtualFolder.CREDS_VIEW)
	String privateKey;

	@ObjectField(type = FieldType.PASSWORD, automaticEncryption = true)
	@ObjectView(value = VirtualFolder.CREDS_VIEW)
	String passphrase;

	public PrivateKeyCredentials() { }
	
	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	
}
