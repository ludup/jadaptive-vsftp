package com.jadaptive.plugins.ssh.vsftp.menus;

import java.util.Arrays;
import java.util.Collection;

import org.pf4j.Extension;

import com.jadaptive.api.ui.menu.ApplicationMenu;
import com.jadaptive.api.ui.menu.ApplicationMenuService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@Extension
public class ConfigureFilesMenu implements ApplicationMenu {

	@Override
	public String getI18n() {
		return "configureFiles.name";
	}

	@Override
	public String getBundle() {
		return VirtualFolder.RESOURCE_KEY;
	}

	@Override
	public String getPath() {
		return "/app/ui/search/virtualFolder";
	}

	@Override
	public Collection<String> getPermissions() {
		return Arrays.asList("virtualFolder.read");
	}

	@Override
	public String getIcon() {
		return "fa-cabinet-filing";
	}

	@Override
	public String getParent() {
		return ApplicationMenuService.CONFIGURATION_MENU;
	}

	@Override
	public String getUuid() {
		return "f1432b86-63f7-4f80-97ff-0a843e43b2e4";
	}

	@Override
	public Integer weight() {
		return 1000;
	}

}
