package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;

import org.jsoup.nodes.Document;

import com.codesmith.webbits.ClasspathResource;
import com.codesmith.webbits.In;
import com.codesmith.webbits.Page;
import com.codesmith.webbits.View;
import com.codesmith.webbits.bootstrap.Bootstrap;
import com.codesmith.webbits.extensions.Widgets;
import com.codesmith.webbits.freemarker.FreeMarker;

@Page({Bootstrap.class, Widgets.class, FreeMarker.class})
@View(contentType = "text/html", paths = { "/upload/{shortCode}" })
@ClasspathResource
public class Upload extends AnonymousPage {
	
	String shortCode;
	
	public String getShortCode() {
		return shortCode;
	}
	
    public Document serviceAnonymous(@In Document contents) throws IOException {
	
		return contents;
	}
}
