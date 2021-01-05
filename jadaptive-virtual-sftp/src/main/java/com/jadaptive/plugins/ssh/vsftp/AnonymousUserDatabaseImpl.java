package com.jadaptive.plugins.ssh.vsftp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.db.TenantAwareObjectDatabase;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.api.tenant.Tenant;
import com.jadaptive.api.tenant.TenantAware;
import com.jadaptive.api.tenant.TenantService;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserDatabaseCapabilities;

@Extension
public class AnonymousUserDatabaseImpl implements AnonymousUserDatabase, TenantAware {

	public static final String ANONYMOUS_USER_UUID = "39ce8047-bd8d-49d8-a17f-5938a23f87fd";
	
	public static final String ANONYMOUS_USERNAME = "anonymous";
	
	@Autowired
	private TenantAwareObjectDatabase<AnonymousUser> anonymousDatabase;
	
	@Autowired
	private TenantService tenantService; 
	
	@Autowired
	private TemplateService templateService; 

	@Override
	public AnonymousUser getUser(String username) {
		if(username.equals(ANONYMOUS_USERNAME)) {
			return anonymousDatabase.get(ANONYMOUS_USER_UUID, AnonymousUser.class);
		}
		return null;
	}

	@Override
	public AnonymousUser getUserByUUID(String uuid) {
		if(uuid.equals(ANONYMOUS_USER_UUID)) {
			return anonymousDatabase.get(uuid, AnonymousUser.class);
		}
		return null;
	}

	@Override
	public Iterable<User> iterateUsers() {
		return new ArrayList<User>(Arrays.asList(
				anonymousDatabase.get(ANONYMOUS_USER_UUID, AnonymousUser.class)));
	}

	@Override
	public ObjectTemplate getUserTemplate() {
		return templateService.get(AnonymousUser.RESOURCE_KEY);
	}

	@Override
	public boolean isDatabaseUser(User user) {
		return user instanceof AnonymousUser;
	}

	@Override
	public Set<UserDatabaseCapabilities> getCapabilities() {
		return new HashSet<>();
	}


	@Override
	public void initializeSystem(boolean newSchema) {
		initializeTenant(tenantService.getSystemTenant(), newSchema);
	}

	@Override
	public void initializeTenant(Tenant tenant, boolean newSchema) {
		if(newSchema) {
			AnonymousUser user = new AnonymousUser();
			user.setUuid(ANONYMOUS_USER_UUID);
			user.setUsername(ANONYMOUS_USERNAME);
			user.setName("Anonymous");
			user.setSystem(true);
			anonymousDatabase.saveOrUpdate(user);
		}
	}

	@Override
	public AnonymousUser getAnonymousUser() {
		return getUserByUUID(ANONYMOUS_USER_UUID);
	}

	@Override
	public Integer weight() {
		return Integer.MIN_VALUE + 1;
	}

	@Override
	public void setPassword(User user, char[] password, boolean passwordChangeRequired) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean verifyPassword(User user, char[] password) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends User> getUserClass() {
		return AnonymousUser.class;
	}

	@Override
	public void deleteUser(User user) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateUser(User user) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createUser(User user, char[] password, boolean forceChange) {
		throw new UnsupportedOperationException();
	};
}
