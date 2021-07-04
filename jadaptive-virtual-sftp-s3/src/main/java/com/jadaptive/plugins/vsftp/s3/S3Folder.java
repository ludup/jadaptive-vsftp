package com.jadaptive.plugins.vsftp.s3;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;

@ObjectDefinition(resourceKey = S3Folder.RESOURCE_KEY, type = ObjectType.COLLECTION, bundle = S3Folder.RESOURCE_KEY)
public class S3Folder extends VirtualFolder {

	private static final long serialVersionUID = 8482791046455758923L;

	public static final String RESOURCE_KEY = "s3Folder";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	S3Credentials credentials; 
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	S3FolderPath path;
	
	public S3FolderPath getPath() {
		return path;
	}
	
	public void setPath(VirtualFolderPath path) {
		this.path = (S3FolderPath) path;
	}
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public S3Credentials getCredentials() {
		return credentials;
	}
	
	public void setCredentials(S3Credentials credentials) {
		this.credentials = credentials;
	}

	@Override
	public String getType() {
		return S3FileScheme.SCHEME_TYPE;
	}
}
