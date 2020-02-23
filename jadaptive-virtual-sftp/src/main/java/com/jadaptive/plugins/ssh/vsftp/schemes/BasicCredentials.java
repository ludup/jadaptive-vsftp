package com.jadaptive.plugins.ssh.vsftp.schemes;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Template(name = "Basic Credentials", resourceKey = "basicCredentials",type = EntityType.OBJECT)
public class BasicCredentials extends VirtualFolderCredentials {

	@Column(name = "Username", description = "The username for this set of credentials", type = FieldType.TEXT)
	String username;
	
	@Column(name = "Password", description = "The password for this set of credentials", type = FieldType.PASSWORD, manualEncryption = true)
	String password;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
