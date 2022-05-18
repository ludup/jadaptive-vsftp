package com.jadaptive.plugins.vsftp.dropbox;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@ObjectDefinition(resourceKey = DropboxCredentials.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT)
public class DropboxCredentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = -1474834193052904187L;

	public  static final String RESOURCE_KEY = "dropboxCredentials";
	
	@ObjectField(required = true, type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.CREDS_VIEW, bundle = DropboxCredentials.RESOURCE_KEY)
	String accessKey;

	@ObjectField(required = true, type = FieldType.PASSWORD, automaticEncryption = true)
	@ObjectView(value = VirtualFolder.CREDS_VIEW, bundle = DropboxCredentials.RESOURCE_KEY)
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
