package com.jadaptive.plugins.ssh.vsftp.schemes;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.template.Entity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Member;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Entity(name = "Windows Credentials", resourceKey = WindowsCredentials.RESOURCE_KEY, type = EntityType.OBJECT)
public class WindowsCredentials extends VirtualFolderCredentials {

	public static final String RESOURCE_KEY =  "windowsCredentials";

	@Member(name = "Domain", description = "The identity's domain", type = FieldType.TEXT)
	String domain;
	
	@Member(name = "Username", description = "The username of the identity", type = FieldType.TEXT)
	String username;
	
	@Member(name = "Password", description = "The password for this identity", type = FieldType.TEXT)
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
	
	
}
