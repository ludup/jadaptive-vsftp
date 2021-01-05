package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;

import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.api.ui.HtmlPage;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabaseImpl;

public abstract class AnonymousPage extends HtmlPage {

	@Autowired
	private PermissionService permissionService; 
	
	@Autowired
	private UserService userService;
	
	@Override
	protected void generateContent(Document contents) throws FileNotFoundException {
	
		permissionService.setupUserContext(userService.getUser(AnonymousUserDatabaseImpl.ANONYMOUS_USERNAME));
		
		try {
			generateAnonymousContent(contents);
		} finally {
			permissionService.clearUserContext();
		}
	}

	protected void generateAnonymousContent(Document contents) {

	}

}
