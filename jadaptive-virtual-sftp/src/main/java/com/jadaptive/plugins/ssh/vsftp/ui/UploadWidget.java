package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.pf4j.Extension;

import com.jadaptive.api.ui.AbstractPageExtension;
import com.jadaptive.api.ui.Page;

@Extension
public class UploadWidget extends AbstractPageExtension {

	
	
	@Override
	public void process(Document document, Page page) throws IOException {
		
	}

	@Override
	public String getName() {
		return "uploadWidget";
	}
}
