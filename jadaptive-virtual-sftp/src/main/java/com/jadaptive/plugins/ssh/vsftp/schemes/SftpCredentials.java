package com.jadaptive.plugins.ssh.vsftp.schemes;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Template(name = "SFTP Credentials", resourceKey = SftpCredentials.RESOURCE_KEY, type = EntityType.OBJECT)
public class SftpCredentials extends VirtualFolderCredentials {

	public static final String RESOURCE_KEY = "sftpCredentials";

	@Column(name = "Password", description = "", type = FieldType.OBJECT_EMBEDDED )
	BasicCredentials basicCredentials;
	
	@Column(name = "Private Key", description = "", type = FieldType.OBJECT_EMBEDDED)
	PrivateKeyCredentials privateKeyCredentials;

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

}
