package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import com.jadaptive.api.ui.wizards.WizardSection;

public class UploadFormSection extends WizardSection {

	public UploadFormSection(String bundle, String name, String resource, Integer weight) {
		super(bundle, name, resource, weight);
	}

	@Override
	public boolean isSystem() {
		return false;
	}
	
	

}
