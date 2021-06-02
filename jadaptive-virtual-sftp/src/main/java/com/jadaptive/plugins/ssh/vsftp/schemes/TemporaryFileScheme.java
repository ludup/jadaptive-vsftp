package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.temp.TemporaryFileProvider;
import org.pf4j.Extension;

@Extension
public class TemporaryFileScheme extends AbstractFileScheme<TemporaryFileProvider> {

	public static final String SCHEME_TYPE = "tmp";
	
	public TemporaryFileScheme() {
		super("Temporary", new TemporaryFileProvider(), "tmp");
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