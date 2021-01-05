package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
import org.pf4j.Extension;

@Extension
public class LocalFileScheme extends AbstractFileScheme {

	public static final String SCHEME_TYPE = "local";
	
	public LocalFileScheme() {
		super("Local Files", new DefaultLocalFileProvider(), "file", "local");
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

