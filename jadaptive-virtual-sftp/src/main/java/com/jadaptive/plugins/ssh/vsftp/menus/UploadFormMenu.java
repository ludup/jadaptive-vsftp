package com.jadaptive.plugins.ssh.vsftp.menus;

import java.util.Arrays;
import java.util.Collection;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.ui.menu.ApplicationMenu;
import com.jadaptive.api.ui.menu.ApplicationMenuService;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadFormService;

@Extension
public class UploadFormMenu implements ApplicationMenu {

	@Autowired
	private ApplicationService applicationService; 
	
	@Override
	public boolean isEnabled() {
		return applicationService.getBean(FeatureEnablementService.class).isEnabled(UploadFormService.UPLOAD_FORMS);
	}
	
	@Override
	public String getResourceKey() {
		return "uploadForms.names";
	}

	@Override
	public String getBundle() {
		return UploadForm.RESOURCE_KEY;
	}

	@Override
	public String getPath() {
		return "/app/ui/search/uploadForms";
	}

	@Override
	public Collection<String> getPermissions() {
		return Arrays.asList("uploadForms.read");
	}

	@Override
	public String getIcon() {
		return "fa-upload";
	}

	@Override
	public String getParent() {
		return ApplicationMenuService.CONFIGURATION_MENU;
	}

	@Override
	public String getUuid() {
		return "c82ab9a6-9db5-4f4e-9c8d-6b1630919067";
	}

	@Override
	public Integer weight() {
		return 1002;
	}

}
