package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.http.HttpFileProvider;
import org.pf4j.Extension;

@Extension
public class HttpsFileScheme extends AbstractFileScheme {

	public static final String SCHEME_TYPE = "https";
	
	public HttpsFileScheme() {
		super("https", new HttpFileProvider(), "https");
	}
	
	@Override
	public String getIcon() {
		return "fab fa-html";
	}

}
