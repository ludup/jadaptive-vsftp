package com.jadaptive.plugins.vsftp.azure;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@ObjectDefinition(resourceKey = AzureCredentials.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = AzureFolder.RESOURCE_KEY)
public class AzureCredentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = -4537246278012737195L;

	public static final String RESOURCE_KEY =  "azureCredentials";

	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.CREDS_VIEW, bundle = AzureCredentials.RESOURCE_KEY)
	String storageAccount;
	
	@ObjectField(type = FieldType.PASSWORD, automaticEncryption = true)
	@ObjectView(value = VirtualFolder.CREDS_VIEW, bundle = AzureCredentials.RESOURCE_KEY)
	String key;

	public String getStorageAccount() {
		return storageAccount;
	}

	public void setStorageAccount(String storageAccount) {
		this.storageAccount = storageAccount;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	
}
