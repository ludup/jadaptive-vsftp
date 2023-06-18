
package com.jadaptive.plugins.vsftp.s3;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;

@ObjectDefinition(resourceKey = S3AccountFolder.RESOURCE_KEY, type = ObjectType.COLLECTION, bundle = S3AccountFolder.RESOURCE_KEY, defaultColumn = "name")
public class S3AccountFolder extends VirtualFolder {

	private static final long serialVersionUID = 8482791046455758923L;

	public static final String RESOURCE_KEY = "s3AccountFolder";

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

	@Override
	public String getType() {
		return S3AccountFileScheme.SCHEME_TYPE;
	}
}
