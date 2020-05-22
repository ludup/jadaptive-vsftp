package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;

import org.jsoup.nodes.Document;

import com.codesmith.webbits.In;
import com.codesmith.webbits.Out;
import com.codesmith.webbits.Page;
import com.codesmith.webbits.Resource;
import com.codesmith.webbits.View;
import com.codesmith.webbits.bootstrap.Bootstrap;
import com.codesmith.webbits.extensions.Widgets;

@Page({Bootstrap.class, Widgets.class})
@View(contentType = "text/html", paths = { "/upload" })
@Resource
public class Upload {
	
	@Out
    public Document service(@In Document contents) throws IOException {
    
		return contents;
	}
}
