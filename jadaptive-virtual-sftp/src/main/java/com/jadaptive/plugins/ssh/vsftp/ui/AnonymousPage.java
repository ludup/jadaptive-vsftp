package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.codesmith.webbits.In;
import com.codesmith.webbits.Out;
import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabaseImpl;

public abstract class AnonymousPage {

	@Autowired
	private PermissionService permissionService; 
	
	@Autowired
	private UserService userService;
	
	@Out
    public Document service(@In Document contents) throws IOException {
	
		permissionService.setupUserContext(userService.getUser(AnonymousUserDatabaseImpl.ANONYMOUS_USERNAME));
		
		try {
			return serviceAnonymous(contents);
		} finally {
			permissionService.clearUserContext();
		}
	}

	protected abstract Document serviceAnonymous(Document contents) throws IOException;
}
