package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.jadaptive.api.app.ApplicationServiceImpl;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.api.ui.Page;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;

public class UploadFormCredentialsSection extends UploadFormSection {

		FileScheme<?> scheme;
		public UploadFormCredentialsSection(FileScheme<?> scheme) {
			super(UploadForm.RESOURCE_KEY, 
					"homeCredentials", 
					"PublicUploadStep2a.html");
			this.scheme = scheme;
		}

		@Override
		protected void processSection(Document document, Element element, Page page) throws IOException {
			
			ObjectTemplate template = ApplicationServiceImpl.getInstance().getBean(TemplateService.class).get(VirtualFolder.RESOURCE_KEY);
			Element content = document.selectFirst("#content");
			content.appendChild(new Element("div")
					.attr("jad:bundle", template.getBundle())
					.attr("jad:id", "objectRenderer")
					.attr("jad:handler", UploadFormWizard.RESOURCE_KEY)
					.attr("jad:disableViews", "true")
					.attr("jad:resourceKey", scheme.getCredentialsTemplate().getResourceKey()));
			
		}
	}