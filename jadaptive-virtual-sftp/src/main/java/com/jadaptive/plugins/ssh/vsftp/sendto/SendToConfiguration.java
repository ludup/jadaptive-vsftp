package com.jadaptive.plugins.ssh.vsftp.sendto;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.SingletonUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.ui.pages.config.ConfigurationItem;

@ObjectDefinition(resourceKey = SendToConfiguration.RESOURCE_KEY, type = ObjectType.SINGLETON)
@ConfigurationItem(bundle = SendToConfiguration.RESOURCE_KEY, icon = "fa-file-export")
public class SendToConfiguration extends SingletonUUIDEntity {

	private static final long serialVersionUID = 8349552815118721956L;
	
	public static final String RESOURCE_KEY = "sendToConfig";
	
	@ObjectField(type= FieldType.BOOL, defaultValue = "false")
	Boolean allowAnonymous;
	
	@ObjectField(type= FieldType.TEXT, defaultValue = "250MB")
	String fileSizeLimit;
	
	@ObjectField(type= FieldType.TEXT, defaultValue = "2G")
	String sourceQuota;
	
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

	public String getFileSizeLimit() {
		return fileSizeLimit;
	}

	public void setFileSizeLimit(String fileSizeLimit) {
		this.fileSizeLimit = fileSizeLimit;
	}

	public String getSourceQuota() {
		return sourceQuota;
	}

	public void setSourceQuota(String sourceQuota) {
		this.sourceQuota = sourceQuota;
	}

}
