package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.Template;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;

@Template(name = "Password Credentials", resourceKey=  PasswordCredentials.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT)
public class PasswordCredentials extends VirtualFolderCredentials {

	public static final String RESOURCE_KEY = "passwordCredentials";
	
	@ObjectField(name = "Username", description = "The username of this user", type = FieldType.TEXT)
	String username;
	
	@ObjectField(name = "Password", description = "The password for this user", type = FieldType.PASSWORD, manualEncryption = true)
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

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	
}
