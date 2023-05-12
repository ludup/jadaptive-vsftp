package com.jadaptive.plugins.ssh.vsftp;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.permissions.AccessDeniedException;
import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.plugins.dashboard.DashboardWidget;

@Extension
public class VirtualFolderWidget implements DashboardWidget {

	@Autowired
	private VirtualFileService fileService;
	
	@Autowired
	private PermissionService permissionService; 
	
	@Override
	public String getIcon() {
		return "fa-folder";
	}

	@Override
	public String getBundle() {
		return VirtualFolder.RESOURCE_KEY;
	}

	@Override
	public String getName() {
		return "folders";
	}

	@Override
	public void renderWidget(Document document, Element element) {

		element.appendChild(new Element("h6").addClass("card-title")
						.appendChild(new Element("span")
								.attr("jad:bundle", getBundle())
								.attr("jad:i18n", "folders.desc")));
		Element e;
		element.appendChild(e = new Element("div").addClass("row"));
		
		for(VirtualFolder folder : fileService.getPersonalFolders()) {
			e.appendChild(
				new Element("div")
					.addClass("col-6")
					.appendChild(
				new Element("a")
					.attr("href", String.format("/app/ui/tree%s", folder.getMountPath()))
						.text(folder.getName())));
		}
		
	}

	@Override
	public Integer weight() {
		return 0;
	}

	@Override
	public boolean wantsDisplay() {
		try {
			permissionService.assertAdministrator();
			return false;
		} catch(AccessDeniedException e) {
			return true;
		}
	}

}
