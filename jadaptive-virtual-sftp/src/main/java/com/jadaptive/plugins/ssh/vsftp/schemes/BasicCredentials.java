package com.jadaptive.plugins.ssh.vsftp.schemes;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@ObjectDefinition(resourceKey = BasicCredentials.RESOURCE_KEY, type = ObjectType.OBJECT)
public class BasicCredentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = 812430748866462973L;

	public static final String RESOURCE_KEY =  "basicCredentials";

	@ObjectField(type = FieldType.TEXT)
	String username;
	
	@ObjectField(type = FieldType.PASSWORD, manualEncryption = true)
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
