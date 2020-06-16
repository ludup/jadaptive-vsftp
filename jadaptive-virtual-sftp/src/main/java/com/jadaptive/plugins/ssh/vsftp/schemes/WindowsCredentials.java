package com.jadaptive.plugins.ssh.vsftp.schemes;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@ObjectDefinition(name = "Windows Credentials", resourceKey = WindowsCredentials.RESOURCE_KEY, type = ObjectType.OBJECT)
public class WindowsCredentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = -3545736477921126047L;

	public static final String RESOURCE_KEY =  "windowsCredentials";

	@ObjectField(name = "Domain", description = "The identity's domain", type = FieldType.TEXT)
	String domain;
	
	@ObjectField(name = "Username", description = "The username of the identity", type = FieldType.TEXT)
	String username;
	
	@ObjectField(name = "Password", description = "The password for this identity", type = FieldType.PASSWORD, manualEncryption = true)
	String password;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

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
