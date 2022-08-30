package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import com.jadaptive.api.ui.wizards.WizardSection;

public class PublicUploadSection extends WizardSection {

	public PublicUploadSection(String bundle, String name, String resource) {
		super(bundle, name, resource);
	}

	@Override
	public boolean isSystem() {
		return false;
	}
	
	

}
