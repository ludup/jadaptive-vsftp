package com.jadaptive.plugins.vsftp.gcs;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.vfs2.FileSystemOptions;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolderPath;
import com.jadaptive.plugins.ssh.vsftp.schemes.AbstractFileScheme;
import com.sshtools.vfs.gcs.GoogleStorageFileProvider;
import com.sshtools.vfs.gcs.GoogleStorageFileSystemConfigBuilder;

@Extension
public class GCSFileScheme extends AbstractFileScheme<GoogleStorageFileProvider> {

	public static final String SCHEME_TYPE = "google";
	
	@Autowired
	private TemplateService templateService; 
	
	public GCSFileScheme() {
		super("Google Storage", new GoogleStorageFileProvider(), "gcs", "google", GCSFolder.RESOURCE_KEY);
	}
	
	@Override
	public boolean requiresCredentials() {
		return true;
	}
	
	@Override
	public boolean createRoot() {
		return false;
	}

	@Override
	public ObjectTemplate getCredentialsTemplate() {
		return templateService.get("googleCredentials");
	}

	@Override
	public Class<? extends VirtualFolderCredentials> getCredentialsClass() {
		return GCSCredentials.class;
	}

	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder vf) throws IOException {
		
		GCSFolder folder = (GCSFolder) vf;
		FileSystemOptions options = new FileSystemOptions();
		if(Objects.nonNull(folder.getCredentials()) && folder.getCredentials() instanceof GCSCredentials) {
			
			String credentials = decryptCredentials(
					((GCSCredentials)folder.getCredentials()).getClientJson());
			GoogleStorageFileSystemConfigBuilder.getInstance().setClientIdJSON(
					options, credentials);
		}
		return options;
	}

	@Override
	public String getIcon() {
		return "fab fa-google";
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
		
		GCSFolder folder = new GCSFolder();
		folder.setName(name);
		folder.setMountPath(mountPath);
		folder.setPath(path);
		folder.setCredentials((GCSCredentials) creds);
		
		return folder;
	}
}
