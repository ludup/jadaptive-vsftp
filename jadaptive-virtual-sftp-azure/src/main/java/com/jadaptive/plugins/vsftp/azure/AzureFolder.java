package com.jadaptive.plugins.vsftp.azure;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;

@ObjectDefinition(resourceKey = AzureFolder.RESOURCE_KEY, type = ObjectType.COLLECTION, bundle = AzureFolder.RESOURCE_KEY, defaultColumn = "name")
public class AzureFolder extends VirtualFolder {

	private static final long serialVersionUID = 8482791046455758923L;

	public static final String RESOURCE_KEY = "azureFolder";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	AzureCredentials credentials; 
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	AzureFolderPath path;
	
	public AzureFolderPath getPath() {
		return path;
	}
	
	public void setPath(VirtualFolderPath path) {
		this.path = (AzureFolderPath) path;
	}
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public AzureCredentials getCredentials() {
		return credentials;
	}
	
	public void setCredentials(AzureCredentials credentials) {
		this.credentials = credentials;
	}

	@Override
	public String getType() {
		return AzureFileScheme.SCHEME_TYPE;
	}
}
