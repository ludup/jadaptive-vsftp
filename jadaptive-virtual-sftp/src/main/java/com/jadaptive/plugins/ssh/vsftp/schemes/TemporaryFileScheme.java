package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.temp.TemporaryFileProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolderPath;

@Extension
public class TemporaryFileScheme extends AbstractFileScheme<TemporaryFileProvider> {

	public static final String SCHEME_TYPE = "tmp";
	
	@Autowired
	private TemplateService templateService; 
	
	public TemporaryFileScheme() {
		super("Temporary", new TemporaryFileProvider(), "tmp");
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
}