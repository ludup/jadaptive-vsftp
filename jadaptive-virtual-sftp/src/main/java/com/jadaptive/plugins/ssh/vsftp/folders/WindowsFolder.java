package com.jadaptive.plugins.ssh.vsftp.folders;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.schemes.SmbFileScheme;
import com.jadaptive.plugins.ssh.vsftp.schemes.WindowsCredentials;

@ObjectDefinition(resourceKey = WindowsFolder.RESOURCE_KEY, 
					bundle = VirtualFolder.RESOURCE_KEY, 
					type = ObjectType.COLLECTION)
public class WindowsFolder extends VirtualFolder {

	private static final long serialVersionUID = 8482791046455758923L;

	public static final String RESOURCE_KEY = "windowsFolder";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED) 
	WindowsFolderPath path;
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	WindowsCredentials credentials; 

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public WindowsCredentials getCredentials() {
		return credentials;
	}
	
	public void setCredentials(VirtualFolderCredentials credentials) {
		this.credentials = (WindowsCredentials) credentials;
	}

	@Override
	public String getType() {
		return SmbFileScheme.SCHEME_TYPE;
	}

	public WindowsFolderPath getPath() {
		return path;
	}

	public void setPath(VirtualFolderPath path) {
		this.path = (WindowsFolderPath) path;
	}
	
	
}
