package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.jadaptive.plugins.ssh.vsftp.links.SharedFile;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFileService;

@Extension
@RequestPage(path = "password-protected/{shortCode}/{filename}")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "freemarker", "jadaptive-utils" } )
@PageProcessors(extensions = { "i18n"} )
public class PasswordProtectedPage extends HtmlPage {

	
	@Autowired
	private SharedFileService sharingService; 
	
	@Autowired
	private ApplicationService applicationService;
	
	String shortCode;
	
	String filename;
	
	@Override
	public String getUri() {
		return "password-protected";
	}
	
	@Override
	protected void beforeProcess(String uri, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		
		applicationService.getBean(FeatureEnablementService.class).assertFeature(SharedFileService.SHARING);
		
		try {
			SharedFile file = sharingService.getDownloadByShortCode(shortCode);
			if(!file.getPasswordProtected() || DownloadPublicFile.hasPassword(request, file)) {
				doRedirect(file);
			}
		} catch(ObjectException e) {
			throw new FileNotFoundException();
		}
	}

	private void doRedirect(SharedFile file) {
		if(StringUtils.isNotBlank(file.getTerms())) {
			throw new UriRedirect(String.format("/app/ui/download-terms/%s/%s", shortCode, filename));
		} else {
			throw new UriRedirect(String.format("/app/ui/download/%s/%s", shortCode, filename));
		}
	}

	@Override
	protected void processPost(Document document, String uri, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String password = request.getParameter("password");
		SharedFile file = sharingService.getDownloadByShortCode(shortCode);
		
		if(file.getPassword().equalsIgnoreCase(password)) {
			request.getSession().setAttribute(file.getShortCode(), password);
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
