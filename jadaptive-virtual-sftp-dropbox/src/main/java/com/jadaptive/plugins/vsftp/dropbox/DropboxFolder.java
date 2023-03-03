package com.jadaptive.plugins.vsftp.dropbox;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.events.GenerateEventTemplates;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;

@ObjectDefinition(resourceKey = DropboxFolder.RESOURCE_KEY, type = ObjectType.COLLECTION, defaultColumn = "name")
@ObjectViews(value = {})
@GenerateEventTemplates(DropboxFolder.RESOURCE_KEY)
public class DropboxFolder extends VirtualFolder {

	private static final long serialVersionUID = 8482791046455758923L;

	public static final String RESOURCE_KEY = "dropboxFolder";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	DropboxCredentials credentials; 
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	DropboxFolderPath path;
	
	public DropboxFolderPath getPath() {
		return path;
	}
	
	public void setPath(VirtualFolderPath path) {
		this.path = (DropboxFolderPath) path;
	}
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public String getType() {
		return DropboxFileScheme.SCHEME_TYPE;
	}
	
	public DropboxCredentials getCredentials() {
		return credentials;
	}
	
	public void setCredentials(DropboxCredentials credentials) {
		this.credentials = credentials;
	}
}
