package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.tar.TarFileProvider;
import org.pf4j.Extension;

@Extension
public class TarFileScheme extends AbstractFileScheme {

	public TarFileScheme() {
		super("Tar File", new TarFileProvider(), "tar");
	}


}
