package com.jadaptive.plugins.ssh.vsftp.menus;

import java.util.Arrays;
import java.util.Collection;

import org.pf4j.Extension;

import com.jadaptive.api.ui.menu.ApplicationMenu;
import com.jadaptive.api.ui.menu.ApplicationMenuService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@Extension
public class BrowserFilesMenu implements ApplicationMenu {

	@Override
	public String getResourceKey() {
		return "browseFiles.name";
	}

	@Override
	public String getBundle() {
		return VirtualFolder.RESOURCE_KEY;
	}

	@Override
	public String getPath() {
		return "/app/ui/tree";
	}

	@Override
	public Collection<String> getPermissions() {
		return Arrays.asList("users.login");
	}

	@Override
	public String getIcon() {
		return "fa-folder-open";
	}

	@Override
	public String getParent() {
		return ApplicationMenuService.RESOURCE_MENU_UUID;
	}

	@Override
	public String getUuid() {
		return "3ac8ab15-0cda-4b73-88cb-ee729e3cba89";
	}

	@Override
	public Integer weight() {
		return 1000;
	}

}
