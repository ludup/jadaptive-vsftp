package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import com.jadaptive.api.session.Session;
import com.jadaptive.api.session.SessionService;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.api.tenant.TenantService;
import com.jadaptive.api.ui.AuthenticatedPage;
import com.jadaptive.api.ui.PageRedirect;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabaseImpl;

public abstract class AnonymousFallbackPage extends AuthenticatedPage {

	@Autowired
	private SessionService sessionService; 
	
	@Autowired
	private TenantService tenantService; 
	
	@Autowired
	private UserService userService; 
	
	@Autowired
	private SessionUtils sessionUtils;
	
	public AnonymousFallbackPage() {
	
	}

	@Override
	protected void beforeProcess(String uri, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		
		try {
			super.beforeProcess(uri, request, response);
		} catch(PageRedirect ex) {
			
			Session session = sessionService.createSession(tenantService.getCurrentTenant(), 
					userService.getUser(AnonymousUserDatabaseImpl.ANONYMOUS_USERNAME), 
					request.getRemoteAddr(), 
					request.getHeader(HttpHeaders.USER_AGENT));
			
			sessionUtils.addSessionCookies(request, response, session);
			
			setCurrentSession(session);
		}

	}
}
