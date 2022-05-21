package com.jadaptive.plugins.vsftp.windows;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.schemes.UsernameAndPasswordCredentials;

@ObjectDefinition(resourceKey = WindowsCredentials.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = WindowsFolder.RESOURCE_KEY)
public class WindowsCredentials extends VirtualFolderCredentials implements UsernameAndPasswordCredentials {

	private static final long serialVersionUID = -3545736477921126047L;

	public static final String RESOURCE_KEY =  "windowsCredentials";

	@ObjectField(type = FieldType.TEXT, automaticEncryption = true)
	@ObjectView(value = VirtualFolder.CREDS_VIEW)
	String domain;
	
	@ObjectField(type = FieldType.TEXT, automaticEncryption = true)
	@ObjectView(value = VirtualFolder.CREDS_VIEW, bundle = VirtualFolder.RESOURCE_KEY)
	String username;
	
	@ObjectField(type = FieldType.PASSWORD, automaticEncryption = true)
	@ObjectView(value = VirtualFolder.CREDS_VIEW, bundle = VirtualFolder.RESOURCE_KEY)
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
