package com.jadaptive.plugins.vsftp.gcs;

import com.jadaptive.api.entity.EntityScope;
import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.template.Template;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Column;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Template(name="Google Credentials", resourceKey = GCSCredentials.RESOURCE_KEY, scope = EntityScope.GLOBAL, type = EntityType.OBJECT)
public class GCSCredentials extends VirtualFolderCredentials {

	public  static final String RESOURCE_KEY = "googleCredentials";
	
	@Column(name = "Client JSON", description = "The contents of the client json file provided by GCS", type = FieldType.TEXT_AREA, manualEncryption = true)
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
