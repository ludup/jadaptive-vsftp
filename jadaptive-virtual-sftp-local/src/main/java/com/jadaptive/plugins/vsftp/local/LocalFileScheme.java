package com.jadaptive.plugins.vsftp.local;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
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
public class LocalFileScheme extends AbstractFileScheme<DefaultLocalFileProvider> {

	public static final String SCHEME_TYPE = "local";
	
	@Autowired
	TemplateService templateService; 
	
	public LocalFileScheme() {
		super(LocalFolder.RESOURCE_KEY, "Local Files", new DefaultLocalFileProvider(), "file", "local");
	}

	@Override
	public String getBundle() {
		return LocalFolder.RESOURCE_KEY;
	}

	@Override
	public URI generateUri(String path) throws URISyntaxException {
		return new File(path.replace('\\', '/')).toURI();
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
		
		LocalFolder folder = new LocalFolder();
		folder.setName(name);
		folder.setMountPath(mountPath);
		folder.setPath(path);
		
		return folder;
	}

	@Override
	public Integer getWeight() {
		return Integer.MIN_VALUE;
	}
	
	
}

