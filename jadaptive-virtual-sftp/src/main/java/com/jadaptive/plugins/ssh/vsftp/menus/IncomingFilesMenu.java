package com.jadaptive.plugins.ssh.vsftp.menus;

import java.util.Arrays;
import java.util.Collection;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.ui.menu.ApplicationMenu;
import com.jadaptive.api.ui.menu.ApplicationMenuService;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadFormService;

@Extension
public class IncomingFilesMenu implements ApplicationMenu {

	@Autowired
	private ApplicationService applicationService;
	
	@Override
	public boolean isEnabled() {
		return applicationService.getBean(FeatureEnablementService.class).isEnabled(UploadFormService.UPLOAD_FORMS);
	}
	
	@Override
	public String getI18n() {
		return "incomingFiles.names";
	}

	@Override
	public String getBundle() {
		return VirtualFolder.RESOURCE_KEY;
	}

	@Override
	public String getPath() {
		return "/app/ui/search/incomingFiles";
	}

	@Override
	public Collection<String> getPermissions() {
		return Arrays.asList("users.login");
	}

	@Override
	public String getIcon() {
		return "fa-inboxes";
	}

	@Override
	public String getParent() {
		return ApplicationMenuService.RESOURCE_MENU_UUID;
	}

	@Override
	public String getUuid() {
		return "ac5be4ce-f754-422c-adc5-6dc3083da590";
	}

	@Override
	public Integer weight() {
		return 1002;
	}

}
