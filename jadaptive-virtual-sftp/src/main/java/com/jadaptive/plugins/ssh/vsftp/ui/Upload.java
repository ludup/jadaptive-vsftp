package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.pf4j.Extension;

import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.api.ui.RequestPage;

@Extension
@RequestPage(path = "public-upload/{shortCode}")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "jadaptive-utils"} )
@PageProcessors(extensions = { "freemarker", "i18n"} )
public class Upload extends AnonymousPage {
	
	String shortCode;
	
	public String getShortCode() {
		return shortCode;
	}
	
    public Document serviceAnonymous(Document contents) throws IOException {
	
		return contents;
	}

	@Override
	public String getUri() {
		return "public-upload";
	}
}
