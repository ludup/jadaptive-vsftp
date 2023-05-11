package com.jadaptive.plugins.ssh.vsftp.ui;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.ui.Html;
import com.jadaptive.plugins.dashboard.DashboardWidget;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.upload.IncomingFile;
import com.jadaptive.plugins.ssh.vsftp.upload.IncomingFileService;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadFormService;

@Extension
public class IncomingFilesDashboard implements DashboardWidget {

	@Autowired
	private IncomingFileService incomingService; 
	
	@Autowired
	private ApplicationService applicationService; 
	
	@Override
	public String getIcon() {
		return "fa-inboxes";
	}
	
	@Override
	public String getBundle() {
		return VirtualFolder.RESOURCE_KEY;
	}

	@Override
	public String getName() {
		return "incomingFiles";
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
				.attr("jad:i18n", "no.incomingFiles"));
		
		int count = 0;
		for(IncomingFile file : incomingService.getLatestFiles()) {
			count++;
			
			element.appendChild(Html.div("row")
					.appendChild(Html.div("col-5")
							.appendChild(Html.a(String.format("/app/ui/view/incomingFiles/%s", file.getUuid())).attr("title", "View the incoming files record")
									.appendChild(Html.span(file.getReference()))))
					.appendChild(Html.div("col-5")
							.appendChild(Html.span(file.getUploadArea())))
							
					.appendChild(Html.div("col-2")
						.appendChild(Html.a(String.format("/app/vfs/incoming/zip/%s", file.getUuid())).attr("title", "Download archive of the incoming files")
							.appendChild(Html.i("far", "fa-download")))));
		}
		
		if(count > 0) {
			title.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
				.attr("jad:i18n", "latestFiles.text");
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
