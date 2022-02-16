package com.jadaptive.plugins.ssh.vsftp.folders;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.schemes.SftpCredentials;
import com.jadaptive.plugins.ssh.vsftp.schemes.SftpFileScheme;
import com.jadaptive.plugins.ssh.vsftp.schemes.SftpOptions;

@ObjectDefinition(resourceKey = SftpFolder.RESOURCE_KEY, 
					bundle = VirtualFolder.RESOURCE_KEY, 
					type = ObjectType.COLLECTION,
					defaultColumn = "name")
public class SftpFolder extends VirtualFolder {

	private static final long serialVersionUID = 8482791046455758923L;

	public static final String RESOURCE_KEY = "sftpFolder";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	SftpFolderPath path;
	
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
	
	public void setCredentials(VirtualFolderCredentials credentials) {
		this.credentials = (SftpCredentials) credentials;
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

	@Override
	public VirtualFolderPath getPath() {
		return path;
	}

	@Override
	public void setPath(VirtualFolderPath path) {
		this.path = (SftpFolderPath) path;
	}
}
