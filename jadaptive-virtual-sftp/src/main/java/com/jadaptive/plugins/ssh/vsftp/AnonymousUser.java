package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.EntityScope;
import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;
import com.jadaptive.api.user.UserImpl;

@Template(name = "Administration Users", resourceKey = AnonymousUser.RESOURCE_KEY, scope = EntityScope.GLOBAL, type = EntityType.COLLECTION)
public class AnonymousUser extends UserImpl {

	public static final String RESOURCE_KEY = "anonymousUser";
	
	@Column(name = "Username", 
			description = "The logon name of the user",
			required = true,
			searchable = true,
			type = FieldType.TEXT, 
			unique = true)
	String username;
	
	@Column(name = "Full Name", 
			description = "The full name of the user",
			required = true,
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
