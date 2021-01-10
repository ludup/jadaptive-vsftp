package com.jadaptive.plugins.ssh.vsftp.folders;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.schemes.SftpCredentials;
import com.jadaptive.plugins.ssh.vsftp.schemes.SftpFileScheme;
import com.jadaptive.plugins.ssh.vsftp.schemes.SftpOptions;

@ObjectDefinition(resourceKey = SftpFolder.RESOURCE_KEY, 
					bundle = VirtualFolder.RESOURCE_KEY, 
					type = ObjectType.COLLECTION)
public class SftpFolder extends VirtualFolder {

	private static final long serialVersionUID = 8482791046455758923L;

	public static final String RESOURCE_KEY = "sftpFolder";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	SftpCredentials credentials; 
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	SftpOptions options;
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public SftpCredentials getCredentials() {
		return credentials;
	}
	
	public void setCredentials(SftpCredentials credentials) {
		this.credentials = credentials;
	}

	public SftpOptions getOptions() {
		return options;
	}

	public void setOptions(SftpOptions options) {
		this.options = options;
	}

	@Override
	public String getType() {
		return SftpFileScheme.SCHEME_TYPE;
	}
}