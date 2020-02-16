package com.jadaptive.plugins.vsftp.s3;

import com.jadaptive.api.entity.EntityScope;
import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.template.Entity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Member;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Entity(name = "S3 Credentials", resourceKey = "s3Credentials", scope = EntityScope.GLOBAL, type = EntityType.OBJECT)
public class S3Credentials extends VirtualFolderCredentials {

	@Member(name="Access Key ID", description = "The access key", required = true, type = FieldType.TEXT)
	String accessKey;

	@Member(name="Secret Access Key", description = "The secret key", required = true, type = FieldType.TEXT)
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
}
