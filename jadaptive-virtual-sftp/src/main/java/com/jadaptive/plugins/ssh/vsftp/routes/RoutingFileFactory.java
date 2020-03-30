package com.jadaptive.plugins.ssh.vsftp.routes;

import java.io.IOException;
import java.net.InetAddress;

import com.jadaptive.api.user.User;
import com.jadaptive.plugins.ssh.vsftp.filters.FilterFileFactory;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.files.vfs.VirtualFile;
import com.sshtools.common.permissions.PermissionDeniedException;

public class RoutingFileFactory extends FilterFileFactory<VirtualFile> {

	private User user;
	private InetAddress remoteAddress;
	private RouteSource[] sources;
	private RoutingServiceImpl router;
	
	public RoutingFileFactory(AbstractFileFactory<VirtualFile> filteredFactory,
			User user,
			InetAddress remoteAddress,
			RouteSource[] sources,
			RoutingServiceImpl router) {
		super(filteredFactory);
		this.user = user;
		this.remoteAddress = remoteAddress;
		this.sources = sources;
		this.router = router;
	}

	@Override
	public VirtualFile getFile(String path) throws PermissionDeniedException, IOException {
		
		for(Route route : router.getRoutes(user, remoteAddress, sources)) {
			if(route.matches(path)) {
				return routeFile(route, path);
			}
		}
		return fileFactory.getFile(path);
	}

	private VirtualFile routeFile(Route route, String path) {
		
		
		return null;
	}

	@Override
	public VirtualFile getDefaultPath() throws PermissionDeniedException, IOException {
		return fileFactory.getDefaultPath();
	}

}
