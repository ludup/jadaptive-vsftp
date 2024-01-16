package com.jadaptive.plugins.debrep;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.SingletonUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;

@ObjectDefinition(resourceKey = DebrepConfiguration.RESOURCE_KEY, type = ObjectType.SINGLETON)
public class DebrepConfiguration extends SingletonUUIDEntity {

	private static final long serialVersionUID = 2007715579113440581L;
	public static final String RESOURCE_KEY = "deprebConfiguration";
	
	@ObjectField(type = FieldType.TEXT)
	String keyServer;
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public String getKeyServer() {
		return keyServer;
	}

	public void setKeyServer(String keyServer) {
		this.keyServer = keyServer;
	}

}
