package com.jadaptive.plugins.vsftp.gcs;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@ObjectDefinition(name="Google Credentials", resourceKey = GCSCredentials.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT)
public class GCSCredentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = -1474834193052904187L;

	public  static final String RESOURCE_KEY = "googleCredentials";
	
	@ObjectField(name = "Client JSON", description = "The contents of the client json file provided by GCS", type = FieldType.TEXT_AREA, manualEncryption = true)
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
