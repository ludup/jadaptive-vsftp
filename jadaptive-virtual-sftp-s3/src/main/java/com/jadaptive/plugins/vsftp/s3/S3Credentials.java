package com.jadaptive.plugins.vsftp.s3;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@ObjectDefinition(resourceKey = S3Credentials.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT)
public class S3Credentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = 4755371207381130509L;

	public static final String RESOURCE_KEY = "s3Credentials";

	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.CREDS_VIEW, bundle = S3Folder.RESOURCE_KEY)
	@Validator(type = ValidationType.REQUIRED)
	String accessKey;

	@ObjectField(type = FieldType.PASSWORD, automaticEncryption = true)
	@ObjectView(value = VirtualFolder.CREDS_VIEW, bundle = S3Folder.RESOURCE_KEY)
	@Validator(type = ValidationType.REQUIRED)
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
