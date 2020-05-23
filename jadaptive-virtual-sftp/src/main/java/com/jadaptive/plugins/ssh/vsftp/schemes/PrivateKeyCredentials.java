package com.jadaptive.plugins.ssh.vsftp.schemes;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Template(name = "Private Key", resourceKey = PrivateKeyCredentials.RESOURCE_KEY, type = ObjectType.OBJECT)
public class PrivateKeyCredentials extends VirtualFolderCredentials {

	public static final String RESOURCE_KEY = "privateKeyCredentials";

	@Column(name = "Private Key", description = "The encoded private key", type = FieldType.TEXT_AREA)
	String privateKey;

	@Column(name = "Passphrase", description = "The private key passphrase", type = FieldType.PASSWORD, manualEncryption = true)
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
