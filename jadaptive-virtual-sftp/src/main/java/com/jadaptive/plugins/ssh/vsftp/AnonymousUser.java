package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.user.User;

@ObjectDefinition(resourceKey = AnonymousUser.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.COLLECTION, creatable = false, updatable = false)
public class AnonymousUser extends User {

	private static final long serialVersionUID = -541052280964473585L;

	public static final String RESOURCE_KEY = "anonymousUser";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
}
