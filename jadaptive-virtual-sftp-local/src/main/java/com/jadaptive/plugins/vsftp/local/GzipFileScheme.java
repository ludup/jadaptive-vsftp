package com.jadaptive.plugins.vsftp.local;

import org.apache.commons.vfs2.provider.gzip.GzipFileProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolderPath;
import com.jadaptive.plugins.ssh.vsftp.schemes.VFSFileScheme;

@Extension
public class GzipFileScheme extends VFSFileScheme<GzipFileProvider> {

	public static final String SCHEME_TYPE = "gzip";
	
	@Autowired
	private TemplateService templateService;
	
	@Override
	public boolean isEnabled() {
		return FeatureEnablementService.isFeatureEnabled(LocalFileScheme.LOCAL_FILES);
	}
	
	public GzipFileScheme() {
		super(GzipFolder.RESOURCE_KEY, "Gzip File", new GzipFileProvider(), "gzip");
	}

	@Override
	public String getIcon() {
		return "fa-solid fa-file-archive";
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
		return 20001;
	}
}
