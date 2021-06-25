package com.jadaptive.plugins.vsftp.gcs;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolderPath;

@ObjectDefinition(resourceKey = GCSFolder.RESOURCE_KEY, type = ObjectType.COLLECTION)
public class GCSFolder extends VirtualFolder {

	private static final long serialVersionUID = 8482791046455758923L;

	public static final String RESOURCE_KEY = "googleFolder";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	GCSCredentials credentials; 
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	LocalFolderPath path;
	
	public LocalFolderPath getPath() {
		return path;
	}
	
	public void setPath(VirtualFolderPath path) {
		this.path = (LocalFolderPath) path;
	}
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public String getType() {
		return GCSFileScheme.SCHEME_TYPE;
	}
	
	public GCSCredentials getCredentials() {
		return credentials;
	}
	
	public void setCredentials(GCSCredentials credentials) {
		this.credentials = credentials;
	}
}
