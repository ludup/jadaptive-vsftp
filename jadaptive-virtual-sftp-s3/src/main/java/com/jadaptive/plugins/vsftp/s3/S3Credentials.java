package com.jadaptive.plugins.vsftp.s3;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Template(name = "S3 Credentials", resourceKey = S3Credentials.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT)
public class S3Credentials extends VirtualFolderCredentials {

	public static final String RESOURCE_KEY = "s3Credentials";

	@Column(name="Access Key ID", description = "The access key", required = true, type = FieldType.TEXT)
	String accessKey;

	@Column(name="Secret Access Key", description = "The secret key", required = true, type = FieldType.PASSWORD, manualEncryption = true)
	String secretKey;
	
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
}
