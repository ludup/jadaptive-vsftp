package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.user.UserDatabase;

public interface AnonymousUserDatabase extends UserDatabase {

	AnonymousUser getAnonymousUser();

}
