package com.jadaptive.plugins.vsftp.gcs;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@ObjectDefinition(resourceKey = GCSCredentials.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT)
public class GCSCredentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = -1474834193052904187L;

	public  static final String RESOURCE_KEY = "googleCredentials";
	
	@ObjectField(type = FieldType.TEXT_AREA, manualEncryption = true)
	@ObjectView(value = VirtualFolder.CREDS_VIEW, bundle = GCSFolder.RESOURCE_KEY)
	String clientJson;
	
	public void setClientJson(String clientJson) {
		this.clientJson = clientJson;
	}
	public String getClientJson() {
		return clientJson;
	}
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
