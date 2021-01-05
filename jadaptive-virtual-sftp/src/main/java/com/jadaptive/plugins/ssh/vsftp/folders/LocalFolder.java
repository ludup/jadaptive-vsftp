package com.jadaptive.plugins.ssh.vsftp.folders;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectServiceBean;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.schemes.LocalFileScheme;

@ObjectDefinition(resourceKey = LocalFolder.RESOURCE_KEY, 
					bundle = VirtualFolder.RESOURCE_KEY, 
					type = ObjectType.COLLECTION)
@ObjectServiceBean(bean = VirtualFileService.class)
public class LocalFolder extends VirtualFolder {

	private static final long serialVersionUID = -2218852440988946601L;

	public static final String RESOURCE_KEY = "localFolder";
	
	public String getType() {
		return LocalFileScheme.SCHEME_TYPE;
	}
}
