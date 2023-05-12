package com.jadaptive.plugins.vsftp.windows;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.smb.SmbFileProvider;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.licensing.FeatureGroup;
import com.jadaptive.plugins.licensing.LicensedFeature;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.schemes.VFSFileScheme;

@Extension
@LicensedFeature(value = SmbFileScheme.WINDOWS_FILES, group = FeatureGroup.PROFESSIONAL)
public class SmbFileScheme extends VFSFileScheme<SmbFileProvider> {

	static Logger log = LoggerFactory.getLogger(SmbFileScheme.class);
	
	public static final String SCHEME_TYPE = "windows";
	
	public static final String WINDOWS_FILES = "Windows Files";
	
	@Autowired
	private TemplateService templateService; 

	@Autowired
	private ApplicationService applicationService; 
	
	@Override
	public boolean isEnabled() {
		return applicationService.getBean(FeatureEnablementService.class).isEnabled(SmbFileScheme.WINDOWS_FILES);
	}
	
	public SmbFileScheme() {
		super(WindowsFolder.RESOURCE_KEY, "Windows CIFS", new SmbFileProvider(), "smb", "windows", "cifs");
	}

	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder vf) throws IOException {

		WindowsFolder folder = (WindowsFolder) vf;
		FileSystemOptions opts = new FileSystemOptions();
		
		if(Objects.nonNull(folder.getCredentials()) && folder.getCredentials() instanceof WindowsCredentials) {
			
			WindowsCredentials creds = (WindowsCredentials) folder.getCredentials();
			String domain = creds.getDomain();
			String username = creds.getUsername();
			String password = decryptCredentials(creds.getPassword());
			
			StaticUserAuthenticator auth = new StaticUserAuthenticator(domain, username, password);
			
	        try {
				DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
			} catch (FileSystemException e) {
				log.error("Could not set credentials on file system options", e);
			}
		}
		return opts;
	}
	
	@Override
	public String getBundle() {
		return WindowsFolder.RESOURCE_KEY;
	}

	@Override
	public boolean requiresCredentials() {
		return true;
	}


	@Override
	public URI generateUri(String path, FileSystemOptions opts) throws URISyntaxException {
		return new URI("smb://" + convertWindowsUNCPath(path));
	}

	private String convertWindowsUNCPath(String path) {
		path = path.replace("\\", "/");
		if(path.startsWith("//")) {
			return path.substring(2);
		}
		return path;
	}

	@Override
	public ObjectTemplate getCredentialsTemplate() {
		return templateService.get(WindowsCredentials.RESOURCE_KEY);
	}

	@Override
	public Class<? extends VirtualFolderCredentials> getCredentialsClass() {
		return WindowsCredentials.class;
	}
	
	@Override
	public VirtualFolder createFolder() {
		return new WindowsFolder();
	}

	@Override
	public void setCredentials(VirtualFolder folder, VirtualFolderCredentials credentials) {
		((WindowsFolder)folder).setCredentials((WindowsCredentials) credentials);
	}
	
	@Override
	public String getIcon() {
		return "fa-brands fa-windows";
	}

	@Override
	public ObjectTemplate getPathTemplate() {
		return templateService.get(WindowsFolderPath.RESOURCE_KEY);
	}

	@Override
	public Class<? extends VirtualFolderPath> getPathClass() {
		return WindowsFolderPath.class;
	}

	@Override
	public VirtualFolder createVirtualFolder(String name, String mountPath, VirtualFolderPath path,
			VirtualFolderCredentials creds) {
		
		WindowsFolder folder = new WindowsFolder();
		folder.setName(name);
		folder.setMountPath(mountPath);
		folder.setPath(path);
		folder.setCredentials(creds);
		
		return folder;
	}
	
	@Override
	public Integer getWeight() {
		return 0;
	}
}
