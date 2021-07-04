package com.jadaptive.plugins.ssh.vsftp.folders;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;

@ObjectDefinition(resourceKey = LocalFolderPath.RESOURCE_KEY, type = ObjectType.OBJECT)
public class LocalFolderPath extends VirtualFolderPath {

	private static final long serialVersionUID = 1918731617426526984L;

	public static final String RESOURCE_KEY = "localFolderPath";
	
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 100, bundle = VirtualFolder.RESOURCE_KEY)
	String destinationUri;
	
	public String getDestinationUri() {
		return destinationUri;
	}

	public void setDestinationUri(String destinationUri) {
		this.destinationUri = destinationUri;
	}
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
