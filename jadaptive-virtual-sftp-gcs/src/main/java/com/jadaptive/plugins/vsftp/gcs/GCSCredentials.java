package com.jadaptive.plugins.vsftp.gcs;

import com.jadaptive.api.entity.EntityScope;
import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.template.Entity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Member;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Entity(name="Google Credentials", resourceKey = "googleCredentials", scope = EntityScope.GLOBAL, type = EntityType.OBJECT)
public class GCSCredentials extends VirtualFolderCredentials {

	@Member(name = "Client JSON", description = "The contents of the client json file provided by GCS", type = FieldType.TEXT_AREA)
	String clientJson;
	
	public void setClientJson(String clientJson) {
		this.clientJson = clientJson;
	}
	public String getClientJson() {
		return clientJson;
	}

}
