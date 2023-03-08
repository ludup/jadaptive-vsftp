package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Document;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.ui.HtmlPage;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.api.ui.RequestPage;
import com.jadaptive.api.ui.UriRedirect;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.sharing.SharedFile;
import com.jadaptive.plugins.ssh.vsftp.sharing.SharedFileService;

@Extension
@RequestPage(path = "download-terms/{shortCode}/{filename}")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "freemarker", "jadaptive-utils" } )
@PageProcessors(extensions = { "i18n"} )
public class TermsAndConditionsPage extends HtmlPage {
	
	@Autowired
	private SharedFileService shareService; 
	
	@Autowired
	private ApplicationService applicationService;
	
	String shortCode;
	
	String filename;
	
	@Override
	public String getUri() {
		return "download-terms";
	}

	@Override
	protected void beforeProcess(String uri, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		
		applicationService.getBean(FeatureEnablementService.class).assertFeature(SharedFileService.SHARING);
		
		try {
			SharedFile file = shareService.getDownloadByShortCode(shortCode);
			if(!file.getAcceptTerms()) {
				doRedirect(file);
			}
		} catch(ObjectException e) {
			throw new FileNotFoundException();
		}
	}

	private void doRedirect(SharedFile file) {
		throw new UriRedirect(String.format("/app/ui/download/%s/%s", shortCode, filename));
	}
	
	

	@Override
	protected void generateContent(Document document) throws IOException {
		
		SharedFile file = shareService.getDownloadByShortCode(shortCode);
		document.selectFirst("#terms").text(file.getTerms());
		super.generateContent(document);
	}

	@Override
	protected void processPost(Document document, String uri, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		Boolean accept = Boolean.parseBoolean(request.getParameter("agree"));
		SharedFile file = shareService.getDownloadByShortCode(shortCode);
		
		if(accept) {
			request.getSession().setAttribute(file.getUuid(), accept);
			doRedirect(file);
		} else {
			showError(document, "sharedFiles", "invalidCredentials.text");
		}
		
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
}
