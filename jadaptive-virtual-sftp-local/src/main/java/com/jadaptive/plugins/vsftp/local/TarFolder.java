package com.jadaptive.plugins.vsftp.local;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.events.GenerateEventTemplates;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectServiceBean;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;

@ObjectDefinition(resourceKey = TarFolder.RESOURCE_KEY, 
					bundle = LocalFolder.RESOURCE_KEY, 
					type = ObjectType.COLLECTION,
					defaultColumn = "name")
@ObjectViews(value = {})
@ObjectServiceBean(bean = VirtualFileService.class)
@GenerateEventTemplates(TarFolder.RESOURCE_KEY)
public class TarFolder extends AbstractLocalFolder {

	private static final long serialVersionUID = -2218852440988946601L;

	public static final String RESOURCE_KEY = "tarFolder";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public String getType() {
		return TarFileScheme.SCHEME_TYPE;
	}
}
