package com.jadaptive.plugins.ssh.vsftp.ui;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.ui.DashboardWidget;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabaseImpl;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.sshtools.common.util.FileUtils;

@Extension
public class PublicUploadDashboard implements DashboardWidget {

	@Autowired
	private VirtualFileService fileService; 
	
	@Override
	public String getIcon() {
		return "globe";
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
		
		List<VirtualFolder> publicFolders = new ArrayList<>();
		for(VirtualFolder folder : fileService.allObjects()) {
			if(folder.getUsers().contains(AnonymousUserDatabaseImpl.ANONYMOUS_USER_UUID)) {
				publicFolders.add(folder);
			}
		}
		
		if(publicFolders.isEmpty()) {
			element.appendChild(new Element("h6")
							.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
							.attr("jad:i18n", "no.publicFolders"));
			return;
		} else {
			element.appendChild(new Element("h6")
							.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
							.attr("jad:i18n", "publicFolders.text"));
		}
		
		for(VirtualFolder folder : publicFolders) {
			String url = FileUtils.checkEndsWithSlash(Request.generateBaseUrl(Request.get())) + "app/ui/public-upload/" + folder.getShortCode();
			element.appendChild(new Element("div")
					.addClass("col-12")
					.appendChild(new Element("a")
							.attr("href", url).text(folder.getName())));
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
