package com.jadaptive.plugins.ssh.vsftp.schemes;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@ObjectDefinition(resourceKey = WindowsCredentials.RESOURCE_KEY, type = ObjectType.OBJECT)
public class WindowsCredentials extends VirtualFolderCredentials {

	private static final long serialVersionUID = -3545736477921126047L;

	public static final String RESOURCE_KEY =  "windowsCredentials";

	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.CREDS_VIEW)
	String domain;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(VirtualFolder.CREDS_VIEW)
	String username;
	
	@ObjectField(type = FieldType.PASSWORD, manualEncryption = true)
	@ObjectView(VirtualFolder.CREDS_VIEW)
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
