package com.jadaptive.plugins.ssh.vsftp.sendto;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.SingletonUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.ui.pages.config.ConfigurationItem;

@ObjectDefinition(resourceKey = SendToConfiguration.RESOURCE_KEY, type = ObjectType.SINGLETON)
@ConfigurationItem(resourceKey = SendToConfiguration.RESOURCE_KEY, icon = "fa-file-export")
public class SendToConfiguration extends SingletonUUIDEntity {

	private static final long serialVersionUID = -5033475662256234916L;

	public static final String RESOURCE_KEY = "sendToConfig";
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	Boolean allowAnonymous;

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public Boolean getAllowAnonymous() {
		return allowAnonymous;
	}

	public void setAllowAnonymous(Boolean allowAnonymous) {
		this.allowAnonymous = allowAnonymous;
	}
	
}
