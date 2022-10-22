package com.jadaptive.plugins.ssh.vsftp.ui.quick;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.ui.QuickSetupItem;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.ui.wizards.UploadFormWizard;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadFormService;

@Extension
public class UploadFormQuickSetupItem implements QuickSetupItem {

	@Autowired
	private ApplicationService applicationService; 
	
	@Override
	public String getBundle() {
		return VirtualFolder.RESOURCE_KEY;
	}

	@Override
	public String getI18n() {
		return "create.public.upload";
	}

	@Override
	public String getLink() {
		return "/app/ui/wizards/" + UploadFormWizard.RESOURCE_KEY;
	}

	@Override
	public boolean isEnabled() {
		return applicationService.getBean(FeatureEnablementService.class).isEnabled(UploadFormService.UPLOAD_FORMS);
	}

}
