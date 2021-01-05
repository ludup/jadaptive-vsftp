package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.http.HttpFileProvider;
import org.pf4j.Extension;

@Extension
public class HttpFileScheme extends AbstractFileScheme {

	public static final String SCHEME_TYPE = "http";
	
	public HttpFileScheme() {
		super("http", new HttpFileProvider(), "http");
	}

	@Override
	public String getIcon() {
		return "fab fa-html";
	}
}
