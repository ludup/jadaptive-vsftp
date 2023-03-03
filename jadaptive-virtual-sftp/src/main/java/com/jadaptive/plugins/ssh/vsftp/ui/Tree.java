package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.api.ui.AuthenticatedPage;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.api.ui.RequestPage;
import com.jadaptive.api.ui.renderers.DropdownInput;

@Extension
@RequestPage(path = "tree/")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "bootstrapTable", "jadaptive-utils" } )
@PageProcessors(extensions = { "i18n"} )
public class Tree extends AuthenticatedPage {
	
	@Autowired
	private SessionUtils sessionUtils;
	
	@Override
	public String getUri() {
		return "tree";
	}

	@Override
	protected void generateAuthenticatedContent(Document document) throws IOException {
		super.generateAuthenticatedContent(document);
		
		sessionUtils.addContentSecurityPolicy(Request.response(), "style-src", "unsafe-inline");
		
		Map<String,String> depths = new HashMap<>();
		depths.put("0", "flat.name");
		depths.put("99", "maximum.name");
		DropdownInput searchDepth = new DropdownInput("searchDepth", "virtualFolder");
		
		document.select("#searchDepthDropdown").first().appendChild(searchDepth.renderInput());
		searchDepth.renderValues(depths, "0", true);
	}
	
	

}
