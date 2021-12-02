package com.jadaptive.plugins.ssh.vsftp.ui;

import java.util.Arrays;
import java.util.Collection;

import org.pf4j.Extension;

import com.jadaptive.api.ui.menu.ApplicationMenu;
import com.jadaptive.api.ui.menu.ApplicationMenuService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.links.PublicDownload;

@Extension
public class PublicDownloadsMenu implements ApplicationMenu {

	@Override
	public String getResourceKey() {
		return "publicDownload.names";
	}

	@Override
	public String getBundle() {
		return PublicDownload.RESOURCE_KEY;
	}

	@Override
	public String getPath() {
		return "/app/ui/table/publicDownload";
	}

	@Override
	public Collection<String> getPermissions() {
		return Arrays.asList("users.login");
	}

	@Override
	public String getIcon() {
		return "download";
	}

	@Override
	public String getParent() {
		return ApplicationMenuService.RESOURCE_MENU_UUID;
	}

	@Override
	public String getUuid() {
		return "840e6b33-af38-4ba3-8918-07bc841a9a6c";
	}

	@Override
	public Integer weight() {
		return 1001;
	}

}
