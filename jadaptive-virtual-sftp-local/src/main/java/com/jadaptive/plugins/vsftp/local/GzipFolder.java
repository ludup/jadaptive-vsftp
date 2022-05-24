package com.jadaptive.plugins.vsftp.local;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectServiceBean;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;

@ObjectDefinition(resourceKey = GzipFolder.RESOURCE_KEY, 
					bundle = LocalFolder.RESOURCE_KEY, 
					type = ObjectType.COLLECTION,
					defaultColumn = "name")
@ObjectServiceBean(bean = VirtualFileService.class)
public class GzipFolder extends AbstractLocalFolder {

	private static final long serialVersionUID = -2218852440988946601L;

	public static final String RESOURCE_KEY = "gzipFolder";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public String getType() {
		return GzipFileScheme.SCHEME_TYPE;
	}
}