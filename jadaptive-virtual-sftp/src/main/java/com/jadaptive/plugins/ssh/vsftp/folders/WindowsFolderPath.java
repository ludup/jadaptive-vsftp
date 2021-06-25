package com.jadaptive.plugins.ssh.vsftp.folders;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;

@ObjectDefinition(resourceKey = WindowsFolderPath.RESOURCE_KEY, type = ObjectType.OBJECT)
public class WindowsFolderPath extends VirtualFolderPath {

	private static final long serialVersionUID = 6251714211294534340L;

	public static final String RESOURCE_KEY = "windowsFolderPath";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
