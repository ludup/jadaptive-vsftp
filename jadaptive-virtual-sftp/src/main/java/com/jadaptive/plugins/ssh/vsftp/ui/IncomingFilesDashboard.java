package com.jadaptive.plugins.ssh.vsftp.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.ui.DashboardWidget;
import com.jadaptive.api.ui.Html;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFile;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFileService;
import com.jadaptive.plugins.ssh.vsftp.upload.IncomingFile;
import com.jadaptive.plugins.ssh.vsftp.upload.IncomingFileService;
import com.sshtools.common.util.FileUtils;

@Extension
public class IncomingFilesDashboard implements DashboardWidget {

	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private IncomingFileService incomingService; 
	
	@Autowired
	private SharedFileService sharingService; 
	
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
	public void renderWidget(Document document, Element element) {
		
		List<SharedFile> shares = new ArrayList<>();
		for(SharedFile share : sharingService.allObjects()) {
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
		
		for(SharedFile share : shares) {
			String downloadURL = FileUtils.checkEndsWithSlash(Request.generateBaseUrl(Request.get())) + "app/ui/incoming/" + share.getShortCode();
		
			
			element.appendChild(Html.div("row")
						.appendChild(Html.div("col-10")
							.appendChild(Html.a(downloadURL).text(share.getName())))
					.appendChild(Html.div("col-2")
						.appendChild(Html.a(downloadURL, "copyURL")
								.appendChild(Html.i("far", "fa-copy")))));
		}

		Collection<IncomingFile> files = incomingService.getLatestFiles();
		if(files.isEmpty()) {
			element.appendChild(new Element("h6")
					.addClass("mt-3")
					.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
					.attr("jad:i18n", "no.incomingFiles"));
			return;
		} else {
			element.appendChild(new Element("h6")
					.addClass("mt-3")
					.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
					.attr("jad:i18n", "latestFiles.text"));
		}
		
		for(IncomingFile file : files) {
			element.appendChild(Html.div("row")
					.appendChild(Html.div("col-5")
						.appendChild(Html.a(String.format("/app/ui/view/incomingFiles/%s", file.getUuid())).appendChild(Html.span(file.getReference()))))
					.appendChild(Html.div("col-5")
						.appendChild(Html.span(file.getUploadArea())))
					.appendChild(Html.div("col-2")
						.appendChild(Html.a(String.format("/app/vfs/incoming/download/%s", file.getUuid()))
							.appendChild(Html.i("far", "fa-download")))));
		}
		
	}

	@Override
	public Integer weight() {
		return 9999;
	}

	@Override
	public boolean wantsDisplay() {
		return true;
	}

}
