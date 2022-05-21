package com.jadaptive.plugins.vsftp.local;

import org.apache.commons.vfs2.provider.jar.JarFileProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolderPath;
import com.jadaptive.plugins.ssh.vsftp.schemes.AbstractFileScheme;

@Extension
public class JarFileScheme extends AbstractFileScheme<JarFileProvider> {

	public static final String SCHEME_TYPE = "jar";
	
	@Autowired
	private TemplateService templateService; 
	
	public JarFileScheme() {
		super("Jar File", new JarFileProvider(), "jar");
	}

	@Override
	public String getIcon() {
		return "far fa-file-archive";
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
