package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.http.HttpFileProvider;
import org.pf4j.Extension;

@Extension
public class HttpsFileScheme extends AbstractFileScheme {

	protected HttpsFileScheme() {
		super("https", new HttpFileProvider(), "https");
	}

}
