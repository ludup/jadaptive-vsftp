package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jadaptive.api.ui.Page;
import com.jadaptive.api.ui.PageCache;
import com.jadaptive.api.wizards.AbstractWizard;

@Extension
@Component
public class PublicUploadWizard extends AbstractWizard<PublicUploadSection> {

	public static final String RESOURCE_KEY = "publicUploadWizard";

	@Autowired
	private PageCache pageCache; 
	
	public static final String STATE_ATTR = "publicUploadWizardState";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	@Override
	public Page getCompletePage() throws FileNotFoundException {
		return pageCache.resolvePage("dashboard");
	}

	@Override
	protected Class<? extends PublicUploadSection> getSectionClass() {
		return PublicUploadSection.class;
	}

	@Override
	protected String getStateAttribute() {
		return STATE_ATTR;
	}

	@Override
	protected PublicUploadSection getFinishSection() {
		return new PublicUploadSection(getResourceKey(), "publicUploadFinish", "PublicUploadFinish.html", -1);
	}

	@Override
	protected PublicUploadSection getStartSection() {
		return new PublicUploadSection(getResourceKey(), "publicUploadStart", "PublicUploadStart.html", -1);
	}

	@Override
	protected Collection<PublicUploadSection> getDefaultSections() {
		return Arrays.asList();
	}


}
