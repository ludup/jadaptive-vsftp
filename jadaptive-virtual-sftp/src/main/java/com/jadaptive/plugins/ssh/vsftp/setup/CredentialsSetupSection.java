package com.jadaptive.plugins.ssh.vsftp.setup;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.jadaptive.api.app.ApplicationServiceImpl;
import com.jadaptive.api.setup.SetupSection;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.api.ui.Page;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

public class CredentialsSetupSection extends SetupSection {

		FileScheme<?> scheme;
		public CredentialsSetupSection(FileScheme<?> scheme) {
			super("setup", 
					"homeCredentials", 
					"HomeCredentials.html");
			this.scheme = scheme;
		}

		@Override
		protected void processSection(Document document, Element element, Page page) throws IOException {
			
			ObjectTemplate template = ApplicationServiceImpl.getInstance().getBean(TemplateService.class).get(VirtualFolder.RESOURCE_KEY);
			Element content = document.selectFirst("#content");
			content.appendChild(new Element("div")
					.attr("jad:bundle", template.getBundle())
					.attr("jad:id", "objectRenderer")
					.attr("jad:handler", "setup")
					.attr("jad:disableViews", "true")
					.attr("jad:resourceKey", scheme.getCredentialsTemplate().getResourceKey()));
			
		}
	}