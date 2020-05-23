package com.jadaptive.plugins.ssh.vsftp.schemes;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Template(name = "Basic Credentials", resourceKey = BasicCredentials.RESOURCE_KEY, type = ObjectType.OBJECT)
public class BasicCredentials extends VirtualFolderCredentials {

	public static final String RESOURCE_KEY =  "basicCredentials";

	@Column(name = "Username", description = "The username for this set of credentials", type = FieldType.TEXT)
	String username;
	
	@Column(name = "Password", description = "The password for this set of credentials", type = FieldType.PASSWORD, manualEncryption = true)
	String password;
	
	public BasicCredentials() { }
	
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

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	
}
