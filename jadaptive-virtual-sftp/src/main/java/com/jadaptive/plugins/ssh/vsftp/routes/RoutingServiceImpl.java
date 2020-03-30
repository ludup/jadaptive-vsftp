package com.jadaptive.plugins.ssh.vsftp.routes;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;

import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.user.User;

public class RoutingServiceImpl extends AuthenticatedService implements RoutingService {

	public Collection<Route> getRoutes(User user, InetAddress remoteAddress, RouteSource[] sources) {
		return Collections.<Route>emptyList();
	}

}
