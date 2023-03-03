package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.ui.ModalPage;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.api.ui.RequestPage;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadFormService;

@Extension
@ModalPage
@RequestPage(path = "incoming/{shortCode}")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "jadaptive-utils"} )
@PageProcessors(extensions = { "freemarker", "i18n"} )
public class Upload extends AnonymousPage {
	
	static Logger log = LoggerFactory.getLogger(Upload.class);
	
	@Autowired
	private UploadFormService uploadService;
	
	@Autowired
	private ApplicationService applicationService;
	
	String shortCode;
	
	public String getShortCode() {
		return shortCode;
	}
	
    public void generateAnonymousContent(Document contents) throws IOException {
    	
    	applicationService.getBean(FeatureEnablementService.class).assertFeature(UploadFormService.UPLOAD_FORMS);
		
    	try {
    		UploadForm folder = uploadService.getFormByShortCode(shortCode);
    		contents.selectFirst("#uploadArea").text(folder.getName());
    		contents.selectFirst("#uploadHolder").attr("jad:shortCode", shortCode);
    	} catch(ObjectException e) {
    		log.error("Failed to lookup shortcode", e);
    		throw new FileNotFoundException(String.format("%s is not a valid folder code", shortCode));
    	}
	}

	@Override
	public String getUri() {
		return "incoming";
	}
}
