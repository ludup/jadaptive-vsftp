package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.pf4j.Extension;

import com.jadaptive.api.ui.AuthenticatedPage;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.api.ui.RequestPage;

@Extension
@RequestPage(path = "upload-files/")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "jadaptive-utils" } )
@PageProcessors(extensions = { "i18n"} )
public class UploadFiles extends AuthenticatedPage {
	
	@Override
	public String getUri() {
		return "upload-files";
	}

	@Override
	protected void generateAuthenticatedContent(Document document) throws IOException {
		super.generateAuthenticatedContent(document);
	}
	
	

}
