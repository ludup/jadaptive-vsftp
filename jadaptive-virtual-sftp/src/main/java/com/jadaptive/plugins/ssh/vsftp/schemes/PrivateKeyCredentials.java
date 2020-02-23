package com.jadaptive.plugins.ssh.vsftp.schemes;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Template(name = "Private Key", resourceKey = "privateKeyCredentials", type = EntityType.OBJECT)
public class PrivateKeyCredentials extends VirtualFolderCredentials {

	@Column(name = "Private Key", description = "The encoded private key", type = FieldType.TEXT_AREA)
	String privateKey;

	@Column(name = "Passphrase", description = "The private key passphrase", type = FieldType.PASSWORD, manualEncryption = true)
	String passphrase;

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
	
	
}
