package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.jar.JarFileProvider;
import org.pf4j.Extension;

@Extension
public class JarFileScheme extends AbstractFileScheme {

	public static final String SCHEME_TYPE = "jar";
	
	public JarFileScheme() {
		super("Jar File", new JarFileProvider(), "jar");
	}

	@Override
	public String getIcon() {
		return "far fa-file-archive";
	}
}
