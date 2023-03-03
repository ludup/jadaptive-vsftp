package com.jadaptive.plugins.vsftp.sftp;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.events.GenerateEventTemplates;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;

@ObjectDefinition(resourceKey = SftpFolder.RESOURCE_KEY, 
					bundle = SftpFolder.RESOURCE_KEY, 
					type = ObjectType.COLLECTION,
					defaultColumn = "name")
@ObjectViews(value = {})
@GenerateEventTemplates(SftpFolder.RESOURCE_KEY)
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
