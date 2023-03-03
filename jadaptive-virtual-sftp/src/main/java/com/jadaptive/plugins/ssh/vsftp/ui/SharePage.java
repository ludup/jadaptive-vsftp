package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.ui.HtmlPage;
import com.jadaptive.api.ui.RequestPage;
import com.jadaptive.api.ui.UriRedirect;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFile;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFileService;

@Extension
@RequestPage(path = "share/{shortCode}")
public class SharePage extends HtmlPage {

	@Autowired
	private SharedFileService downloadService; 

	@Autowired
	private ApplicationService applicationService; 
	
	String shortCode;

	
	@Override
	protected void beforeProcess(String uri, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		
		applicationService.getBean(FeatureEnablementService.class).assertFeature(SharedFileService.SHARING);
		
		try {
			SharedFile file = downloadService.getDownloadByShortCode(shortCode);
			throw new UriRedirect(String.format("/app/ui/download/%s/%s", shortCode, file.getFilename()));
		} catch(ObjectException e) {
			throw new FileNotFoundException();
		}
	}
	
	@Override
	public String getUri() {
		return "share";
	}

}
