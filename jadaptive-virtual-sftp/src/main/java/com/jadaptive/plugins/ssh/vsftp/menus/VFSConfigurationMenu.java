package com.jadaptive.plugins.ssh.vsftp.menus;

import java.util.Arrays;
import java.util.Collection;

import org.pf4j.Extension;

import com.jadaptive.api.ui.menu.ApplicationMenu;
import com.jadaptive.api.ui.menu.ApplicationMenuService;
import com.jadaptive.plugins.ssh.vsftp.VFSConfiguration;

@Extension
public class VFSConfigurationMenu implements ApplicationMenu {

	@Override
	public String getResourceKey() {
		return "vfsConfiguration.name";
	}

	@Override
	public String getBundle() {
		return VFSConfiguration.RESOURCE_KEY;
	}

	@Override
	public String getPath() {
		return "/app/ui/config/vfsConfiguration";
	}

	@Override
	public Collection<String> getPermissions() {
		return Arrays.asList("vfsConfiguration.read");
	}

	@Override
	public String getIcon() {
		return "folder-open";
	}

	@Override
	public String getParent() {
		return ApplicationMenuService.SYSTEM_MENU_UUID;
	}

	@Override
	public String getUuid() {
		return "68a66aa7-d98b-4542-8c10-20a18d61168b";
	}

	@Override
	public Integer weight() {
		return 1000;
	}

}
