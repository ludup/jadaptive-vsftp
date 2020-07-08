package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;

@ObjectDefinition(resourceKey=  PasswordCredentials.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT)
public class PasswordCredentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = -2093419198418767948L;

	public static final String RESOURCE_KEY = "passwordCredentials";
	
	@ObjectField(type = FieldType.TEXT)
	String username;
	
	@ObjectField(type = FieldType.PASSWORD, manualEncryption = true)
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
