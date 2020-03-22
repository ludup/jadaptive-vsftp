package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.EntityScope;
import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.template.Template;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Column;

@Template(name = "Password Credentials", resourceKey=  PasswordCredentials.RESOURCE_KEY, scope = EntityScope.GLOBAL, type = EntityType.OBJECT)
public class PasswordCredentials extends VirtualFolderCredentials {

	public static final String RESOURCE_KEY = "passwordCredentials";
	
	@Column(name = "Username", description = "The username of this user", type = FieldType.TEXT)
	String username;
	
	@Column(name = "Password", description = "The password for this user", type = FieldType.PASSWORD, manualEncryption = true)
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