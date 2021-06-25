package com.jadaptive.plugins.ssh.vsftp.schemes;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolder;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolderPath;

@Extension
public class LocalFileScheme extends AbstractFileScheme<DefaultLocalFileProvider> {

	public static final String SCHEME_TYPE = "local";
	
	@Autowired
	TemplateService templateService; 
	
	public LocalFileScheme() {
		super("Local Files", new DefaultLocalFileProvider(), "file", "local", LocalFolder.RESOURCE_KEY);
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
}

