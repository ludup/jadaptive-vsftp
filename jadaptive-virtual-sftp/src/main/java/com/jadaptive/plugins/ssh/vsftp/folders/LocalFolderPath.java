package com.jadaptive.plugins.ssh.vsftp.folders;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;

@ObjectDefinition(resourceKey = LocalFolderPath.RESOURCE_KEY, type = ObjectType.OBJECT)
public class LocalFolderPath extends VirtualFolderPath {

	private static final long serialVersionUID = 1918731617426526984L;

	public static final String RESOURCE_KEY = "localFolderPath";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
