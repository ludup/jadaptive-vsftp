package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import java.util.Collection;
import java.util.HashSet;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.UUIDEntity;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.user.User;

@ObjectDefinition(resourceKey = UploadFormAssignment.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT, bundle = UploadFormWizard.RESOURCE_KEY)
public class UploadFormAssignment extends UUIDEntity {

	private static final long serialVersionUID = -8207554236785132336L;

	public static final String RESOURCE_KEY = "publicUploadAssignment";
	
	@ObjectField(defaultValue = "false", 
			type = FieldType.OBJECT_REFERENCE,
			references = "roles")
	Collection<Role> roles = new HashSet<>();
	
	@ObjectField(defaultValue = "false", 
			type = FieldType.OBJECT_REFERENCE,
			references = "users")
	Collection<User> users = new HashSet<>();
	
	public Collection<Role> getRoles() {
		return roles;
	}
	
	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}
	
	public Collection<User> getUsers() {
		return users;
	}
	
	public void setUsers(Collection<User> users) {
		this.users = users;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
}
