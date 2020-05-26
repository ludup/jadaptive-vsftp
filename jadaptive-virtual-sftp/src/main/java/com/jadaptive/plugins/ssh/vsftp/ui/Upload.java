package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codesmith.webbits.In;
import com.codesmith.webbits.Out;
import com.codesmith.webbits.Page;
import com.codesmith.webbits.Resource;
import com.codesmith.webbits.View;
import com.codesmith.webbits.bootstrap.Bootstrap;
import com.codesmith.webbits.extensions.Widgets;
import com.codesmith.webbits.freemarker.FreeMarker;
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.app.ui.AbstractPage;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@Page({Bootstrap.class, Widgets.class, FreeMarker.class})
@View(contentType = "text/html", paths = { "/upload/{shortCode}" })
@Resource
@Component
public class Upload extends AbstractPage {
	
	String shortCode;
	
	@Autowired
	private VirtualFileService fileService; 
	
	public String getShortCode() {
		return shortCode;
	}
	
	@Out
    public Document service(@In Document contents) throws IOException {
	
//		try {
//			VirtualFolder folder = fileService.getVirtualFolderByShortCode(shortCode);
//			
//		} catch(ObjectNotFoundException e) {
//			throw new FileNotFoundException();
//		}
	
		return contents;
	}
}
