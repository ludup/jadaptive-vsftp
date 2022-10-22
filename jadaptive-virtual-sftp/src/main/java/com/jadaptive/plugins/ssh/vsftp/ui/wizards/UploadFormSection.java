package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import com.jadaptive.api.ui.wizards.WizardSection;

public class UploadFormSection extends WizardSection {

	public UploadFormSection(String bundle, String name, String resource) {
		super(bundle, name, resource);
	}

	@Override
	public boolean isSystem() {
		return false;
	}
	
	

}
