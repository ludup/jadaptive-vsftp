package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.tar.TarFileProvider;
import org.pf4j.Extension;

@Extension
public class TarFileScheme extends AbstractFileScheme<TarFileProvider> {

	public static final String SCHEME_TYPE = "tar";
	
	public TarFileScheme() {
		super("Tar File", new TarFileProvider(), "tar");
	}
	
	@Override
	public String getIcon() {
		return "far fa-file-archive";
	}

}
