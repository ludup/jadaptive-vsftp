
package com.jadaptive.plugins.ssh.vsftp.menus;

import java.util.Collection;
import java.util.Collections;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.ui.menu.ApplicationMenu;
import com.jadaptive.api.ui.menu.ApplicationMenuService;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFile;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFileService;

@Extension
public class SharedFilesMenu implements ApplicationMenu {

	@Autowired
	private ApplicationService applicationService; 
	
	@Override
	public boolean isEnabled() {
		return applicationService.getBean(FeatureEnablementService.class).isEnabled(SharedFileService.SHARING);
	}
	
	@Override
	public String getI18n() {
		return "sharedFiles.names";
	}

	@Override
	public String getBundle() {
		return SharedFile.RESOURCE_KEY;
	}

	@Override
	public String getPath() {
		return "/app/ui/search/sharedFiles";
	}

	@Override
	public Collection<String> getPermissions() {
		return Collections.emptyList();
	}

	@Override
	public String getIcon() {
		return "fa-share-nodes";
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
