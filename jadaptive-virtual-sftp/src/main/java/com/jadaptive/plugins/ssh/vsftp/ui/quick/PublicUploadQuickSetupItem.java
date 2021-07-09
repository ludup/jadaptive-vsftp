package com.jadaptive.plugins.ssh.vsftp.ui.quick;

import org.pf4j.Extension;

import com.jadaptive.api.ui.QuickSetupItem;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.ui.wizards.PublicUploadWizard;

@Extension
public class PublicUploadQuickSetupItem implements QuickSetupItem {

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
		return "/app/ui/wizards/" + PublicUploadWizard.RESOURCE_KEY;
	}

}
