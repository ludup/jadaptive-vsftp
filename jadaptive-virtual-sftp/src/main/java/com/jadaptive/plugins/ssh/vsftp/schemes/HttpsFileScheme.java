package com.jadaptive.plugins.ssh.vsftp.schemes;

import org.apache.commons.vfs2.provider.http.HttpFileProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolderPath;

@Extension
public class HttpsFileScheme extends AbstractFileScheme<HttpFileProvider> {

	public static final String SCHEME_TYPE = "https";
	
	@Autowired
	private TemplateService templateService;
	
	public HttpsFileScheme() {
		super("https", new HttpFileProvider(), "https");
	}
	
	@Override
	public String getIcon() {
		return "fab fa-html";
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
