package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.ui.HtmlPage;
import com.jadaptive.api.ui.PageCache;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.api.ui.RequestPage;
import com.jadaptive.api.ui.UriRedirect;
import com.jadaptive.api.ui.wizards.WizardService;
import com.jadaptive.api.ui.wizards.WizardState;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;
import com.jadaptive.utils.ObjectUtils;
import com.sshtools.common.util.FileUtils;

@Extension
@RequestPage(path = "public-upload-wizard-complete")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "jadaptive-utils"})
@PageProcessors(extensions = { "i18n"} )
public class UploadFormComplete extends HtmlPage {

	@Autowired
	private WizardService wizardService; 
	
	@Autowired
	private PageCache pageCache;
	
	@Override
	public String getUri() {
		return "public-upload-wizard-complete";
	}

	@Override
	protected void generateContent(Document document) throws IOException {
		
		WizardState state = wizardService.getWizard(UploadFormWizard.RESOURCE_KEY).getState(Request.get());
		
		if(!state.isFinished()) {
			throw new UriRedirect("/app/ui/wizard/" + state.getResourceKey());
		}
		
		UploadForm form = ObjectUtils.assertObject(state.getCompletedObject(), UploadForm.class);
		String shortcode = form.getShortCode();
		String generatedURL = FileUtils.checkEndsWithSlash(Request.generateBaseUrl(Request.get())) + "app/ui/incoming/" + shortcode;
		Element url = document.selectFirst("#publicURL");
		url.attr("href", generatedURL);
		url.text(generatedURL);
		
		wizardService.getWizard(UploadFormWizard.RESOURCE_KEY).clearState(Request.get());
		
		super.generateContent(document);
	}

}
