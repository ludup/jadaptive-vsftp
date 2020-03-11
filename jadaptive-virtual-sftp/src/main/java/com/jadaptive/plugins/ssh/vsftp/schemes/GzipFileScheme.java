package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.gzip.GzipFileProvider;
import org.pf4j.Extension;

@Extension
public class GzipFileScheme extends AbstractFileScheme {

	public GzipFileScheme() {
		super("Gzip File", new GzipFileProvider(), "gzip");
	}

}
