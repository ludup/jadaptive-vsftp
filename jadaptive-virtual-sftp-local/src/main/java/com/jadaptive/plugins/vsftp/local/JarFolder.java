package com.jadaptive.plugins.vsftp.local;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.events.GenerateEventTemplates;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectServiceBean;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;

@ObjectDefinition(resourceKey = JarFolder.RESOURCE_KEY, 
					bundle = LocalFolder.RESOURCE_KEY, 
					type = ObjectType.COLLECTION,
					defaultColumn = "name")
@ObjectViews(value = {})
@ObjectServiceBean(bean = VirtualFileService.class)
@GenerateEventTemplates(JarFolder.RESOURCE_KEY)
public class JarFolder extends AbstractLocalFolder {

	private static final long serialVersionUID = -2218852440988946601L;

	public static final String RESOURCE_KEY = "jarFolder";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public String getType() {
		return JarFileScheme.SCHEME_TYPE;
	}
}
