package com.jadaptive.plugins.vsftp.dropbox;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@ObjectDefinition(resourceKey = DropboxCredentials.RESOURCE_KEY, scope = ObjectScope.GLOBAL, 
	type = ObjectType.OBJECT, bundle = DropboxFolder.RESOURCE_KEY)
public class DropboxCredentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = -1474834193052904187L;

	public  static final String RESOURCE_KEY = "dropboxCredentials";
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.CREDS_VIEW, bundle = DropboxFolder.RESOURCE_KEY)
	String accessKey;

	@ObjectField(type = FieldType.TEXT, automaticEncryption = true, hidden = true)
	String refreshKey;
	
	public String getAccessKey() {
		return accessKey;
	}
	
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	
	public String getRefreshKey() {
		return refreshKey;
	}

	public void setRefreshKey(String refreshKey) {
		this.refreshKey = refreshKey;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
