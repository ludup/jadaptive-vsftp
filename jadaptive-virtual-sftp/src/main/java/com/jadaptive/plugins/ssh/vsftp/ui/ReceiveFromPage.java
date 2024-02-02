package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.pf4j.Extension;

import com.jadaptive.api.ui.HtmlPage;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.api.ui.RequestPage;

@Extension
@RequestPage(path = "recv/{shareCode}")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "bootstrapTable", "jadaptive-utils" } )
@PageProcessors(extensions = { "i18n"} )
public class ReceiveFromPage extends HtmlPage {

	String shareCode;
	
	@Override
	public String getUri() {
		return "recv";
	}

	@Override
	protected void generateContent(Document document) throws IOException {
		
		super.generateContent(document);
		
		document.selectFirst("#shareCode").val(shareCode);
	}

	
}
