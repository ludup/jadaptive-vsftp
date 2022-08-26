package com.jadaptive.plugins.ssh.vsftp.ui;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.ui.DashboardWidget;
import com.jadaptive.api.ui.Html;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.upload.IncomingFile;
import com.jadaptive.plugins.ssh.vsftp.upload.IncomingFileService;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadFormService;
import com.sshtools.common.util.FileUtils;

@Extension
public class IncomingFilesDashboard implements DashboardWidget {

	@Autowired
	private IncomingFileService incomingService; 
	
	@Autowired
	private UploadFormService uploadService; 
	
	@Autowired
	private ApplicationService applicationService; 
	
	@Override
	public String getIcon() {
		return "inboxes";
	}
	
	@Override
	public String getBundle() {
		return VirtualFolder.RESOURCE_KEY;
	}

	@Override
	public String getName() {
		return "publicUpload";
	}
	
	@Override
	public boolean hasHelp() {
		return false;
	}

	@Override
	public void renderWidget(Document document, Element element) {
		
		List<UploadForm> shares = new ArrayList<>();
		for(UploadForm share : uploadService.getUserForms()) {
			shares.add(share);
		}
		
		if(shares.isEmpty()) {
			element.appendChild(new Element("h6")
							.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
							.attr("jad:i18n", "no.publicFolders"));
			return;
		} else {
			element.appendChild(new Element("h6")
							.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
							.attr("jad:i18n", "publicFolders.text"));
		}
		
		for(UploadForm share : shares) {
		
			String downloadURL = FileUtils.checkEndsWithSlash(Request.generateBaseUrl(Request.get())) + "app/ui/incoming/" + share.getShortCode();

			element.appendChild(Html.div("row")
						.appendChild(Html.div("col-10")
							.appendChild(Html.a(String.format("/app/ui/tree%s", share.getVirtualPath())).text(share.getName()).attr("title", "Browse upload area")))
					.appendChild(Html.div("col-2")
						.appendChild(Html.a(downloadURL, "copyURL").attr("title", "Copy URL to clipboard")
								.appendChild(Html.i("far fa-fw", "fa-copy")))
						.appendChild(Html.a(downloadURL, "").attr("title", "Goto upload area")
								.appendChild(Html.i("far fa-fw", "fa-link")))));
		
		}

		Element title;
		element.appendChild(title = new Element("h6")
				.addClass("mt-3")
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
