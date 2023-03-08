package com.jadaptive.plugins.vsftp.local;

import org.apache.commons.vfs2.provider.temp.TemporaryFileProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolderPath;
import com.jadaptive.plugins.ssh.vsftp.schemes.AbstractFileScheme;

@Extension
public class TemporaryFileScheme extends AbstractFileScheme<TemporaryFileProvider> {

	public static final String SCHEME_TYPE = "tmp";
	
	@Autowired
	private TemplateService templateService; 
	
	@Autowired
	private FeatureEnablementService featureService; 
	
	@Override
	public boolean isEnabled() {
		return featureService.isEnabled(LocalFileScheme.LOCAL_FILES);
	}
	
	public TemporaryFileScheme() {
		super(TemporaryFolder.RESOURCE_KEY, "Temporary", new TemporaryFileProvider(), "tmp");
	}
	
	@Override
	public boolean createRoot() {
		return true;
	}
	
	@Override
	public String getIcon() {
		return "far fa-hdd";
	}

	@Override
	public ObjectTemplate getPathTemplate() {
		return templateService.get(LocalFolderPath.RESOURCE_KEY);
	}

	@Override
	public Class<? extends VirtualFolderPath> getPathClass() {
		return LocalFolderPath.class;
	}
	
	@Override
	public VirtualFolder createVirtualFolder(String name, String mountPath, VirtualFolderPath path,
			VirtualFolderCredentials creds) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Integer getWeight() {
		return 10000;
	}
}