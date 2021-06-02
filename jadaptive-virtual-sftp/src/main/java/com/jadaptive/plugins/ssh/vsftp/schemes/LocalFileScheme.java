package com.jadaptive.plugins.ssh.vsftp.schemes;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
import org.pf4j.Extension;

@Extension
public class LocalFileScheme extends AbstractFileScheme<DefaultLocalFileProvider> {

	public static final String SCHEME_TYPE = "local";
	
	public LocalFileScheme() {
		super("Local Files", new DefaultLocalFileProvider(), "file", "local");
	}

	@Override
	public URI generateUri(String path) throws URISyntaxException {
		return new File(path.replace('\\', '/')).toURI();
	}
	
	@Override
	public boolean createRoot() {
		return true;
	}
	
	@Override
	public String getIcon() {
		return "far fa-hdd";
	}
}

