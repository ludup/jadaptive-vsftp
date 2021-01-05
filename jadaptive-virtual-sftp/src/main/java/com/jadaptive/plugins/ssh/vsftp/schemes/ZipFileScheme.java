package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.zip.ZipFileProvider;
import org.pf4j.Extension;

@Extension
public class ZipFileScheme extends AbstractFileScheme {

	public static final String SCHEME_TYPE = "zip";
	
	public ZipFileScheme() {
		super("Zip File", new ZipFileProvider(), "zip");
	}

	@Override
	public String getIcon() {
		return "far fa-file-archive";
	}
}
