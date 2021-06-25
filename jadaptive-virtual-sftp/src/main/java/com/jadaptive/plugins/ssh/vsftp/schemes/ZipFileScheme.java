package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.zip.ZipFileProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolderPath;

@Extension
public class ZipFileScheme extends AbstractFileScheme<ZipFileProvider> {

	public static final String SCHEME_TYPE = "zip";
	
	@Autowired
	private TemplateService templateService; 
	
	public ZipFileScheme() {
		super("Zip File", new ZipFileProvider(), "zip");
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
}
