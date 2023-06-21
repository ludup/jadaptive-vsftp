package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.SingletonUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;

@ObjectDefinition(resourceKey = VFSConfiguration.RESOURCE_KEY, type = ObjectType.SINGLETON, aliases = { VFSConfiguration.RESOURCE_KEY + ".updated" })
public class VFSConfiguration extends SingletonUUIDEntity {

	private static final long serialVersionUID = 8028157806019041591L;

	public static final String GENERAL_VIEW = "general";
	
	public static final String RESOURCE_KEY = "vfsConfiguration";
	
	@ObjectField(type = FieldType.ENUM, defaultValue = "SHA256")
	@ObjectView(value = GENERAL_VIEW)
	ContentHash defaultHash;
	
	@Override
	public String getResourceKey() {
		return "vfsConfiguration";
	}

	public ContentHash getDefaultHash() {
		return defaultHash;
	}

	public void setDefaultHash(ContentHash defaultHash) {
		this.defaultHash = defaultHash;
	}
	
}
