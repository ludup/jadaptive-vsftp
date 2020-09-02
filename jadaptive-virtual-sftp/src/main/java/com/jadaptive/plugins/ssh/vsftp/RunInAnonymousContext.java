package com.jadaptive.plugins.ssh.vsftp;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codesmith.webbits.Out;
import com.jadaptive.api.permissions.AuthenticatedService;

@Service
public class RunInAnonymousContext extends AuthenticatedService {

	@Autowired
	private AnonymousUserDatabase anonymousDatabase;
	
	@Out
	void runAnon(Callable<?> task) throws Exception {
		setupUserContext(anonymousDatabase.getAnonymousUser());
		try {
			task.call();
		}
		finally {
			clearUserContext();
		}
	}
}
