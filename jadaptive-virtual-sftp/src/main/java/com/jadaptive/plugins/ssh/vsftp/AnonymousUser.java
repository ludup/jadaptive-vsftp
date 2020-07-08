package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.user.UserImpl;

@ObjectDefinition(resourceKey = AnonymousUser.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.COLLECTION)
public class AnonymousUser extends UserImpl {

	private static final long serialVersionUID = -541052280964473585L;

	public static final String RESOURCE_KEY = "anonymousUser";
	
	@ObjectField(required = true,
			searchable = true,
			type = FieldType.TEXT, 
			unique = true)
	String username;
	
	@ObjectField(required = true,
			searchable = true,
			type = FieldType.TEXT)
	String name;

	
	@Override
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
