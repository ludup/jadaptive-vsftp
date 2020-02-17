package com.jadaptive.plugins.vsftp.gcs;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.vfs2.FileSystemOptions;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.api.template.EntityTemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.schemes.AbstractFileScheme;
import com.sshtools.vfs.gcs.GoogleStorageFileProvider;
import com.sshtools.vfs.gcs.GoogleStorageFileSystemConfigBuilder;

@Extension
public class GCSFileScheme extends AbstractFileScheme {

	@Autowired
	private EntityTemplateService templateService; 
	
	public GCSFileScheme() {
		super("Google Storage", new GoogleStorageFileProvider(), "gcs", "google");
	}
	
	@Override
	public boolean requiresCredentials() {
		return true;
	}

	@Override
	public EntityTemplate getCredentialsTemplate() {
		return templateService.get("googleCredentials");
	}

	@Override
	public Class<? extends VirtualFolderCredentials> getCredentialsClass() {
		return GCSCredentials.class;
	}

	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder folder) throws IOException {
		
		FileSystemOptions options = new FileSystemOptions();
		if(Objects.nonNull(folder.getCredentials()) && folder.getCredentials() instanceof GCSCredentials) {
			
			String credentials = ((GCSCredentials)folder.getCredentials()).getClientJson();
			GoogleStorageFileSystemConfigBuilder.getInstance().setClientIdJSON(
					options, credentials);
		}
		return options;
	}
}
