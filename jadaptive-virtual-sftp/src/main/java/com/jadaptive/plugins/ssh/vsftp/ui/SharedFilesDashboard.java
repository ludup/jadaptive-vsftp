package com.jadaptive.plugins.ssh.vsftp.ui;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.api.ui.DashboardWidget;
import com.jadaptive.api.ui.Html;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.sharing.SharedFile;
import com.jadaptive.plugins.ssh.vsftp.sharing.SharedFileService;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadFormService;

@Extension
public class SharedFilesDashboard implements DashboardWidget {

	@Autowired
	private SharedFileService shareService; 
	
	@Autowired
	private ApplicationService applicationService; 
	
	@Autowired
	private SessionUtils sessionUtils;
	
	@Override
	public String getIcon() {
		return "fa-share-nodes";
	}
	
	@Override
	public String getBundle() {
		return VirtualFolder.RESOURCE_KEY;
	}

	@Override
	public String getName() {
		return "sharedFiles";
	}
	
	@Override
	public boolean hasHelp() {
		return false;
	}

	@Override
	public void renderWidget(Document document, Element element) {

		Element title;
		element.appendChild(title = new Element("h6")
				.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
				.attr("jad:i18n", "no.sharedFiles"));
		
		int count = 0;
		for(SharedFile file : shareService.getUserShares()) {
			count++;
			
			element.appendChild(Html.div("row")
					.appendChild(Html.div("col-6")
							.appendChild(Html.a(String.format("/app/ui/share/%s", file.getShortCode()))
									.appendChild(Html.span(file.getName()))))
					.appendChild(Html.div("col-6")
							.appendChild(Html.a(String.format("/app/ui/share/%s", file.getShortCode()))
								.appendChild(Html.i("fa-regular", "fa-link")))
								.appendChild(Html.a("#").attr("data-url", "/app/api/objects/sharedFiles/" + file.getUuid())
										.addClass("deleteAction ms-2")
										.attr("data-name", file.getName())
										.appendChild(Html.i("fa-regular", "fa-trash")))));
			
		}
		
		if(count > 0) {
			title.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
				.attr("jad:i18n", "mySharedFiles.text");
			
			element.appendChild(Html.input("hidden", "csrftoken", 
					sessionUtils.setupCSRFToken(Request.get()))
					.attr("id", "csrftoken"));
		}
	}

	@Override
	public Integer weight() {
		return 9999;
	}

	@Override
	public boolean wantsDisplay() {
		return applicationService.getBean(FeatureEnablementService.class).isEnabled(UploadFormService.UPLOAD_FORMS);
	}

}
