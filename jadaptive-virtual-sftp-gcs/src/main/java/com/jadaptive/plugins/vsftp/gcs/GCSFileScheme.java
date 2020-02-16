package com.jadaptive.plugins.vsftp.gcs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.FileProvider;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.api.template.EntityTemplateService;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.sshtools.vfs.gcs.GoogleStorageFileProvider;
import com.sshtools.vfs.gcs.GoogleStorageFileSystemConfigBuilder;

@Extension
public class GCSFileScheme implements FileScheme {

	@Autowired
	private EntityTemplateService templateService; 
	
	private GoogleStorageFileProvider provider = new GoogleStorageFileProvider();
	
	@Override
	public boolean requiresCredentials() {
		return true;
	}

	@Override
	public Set<String> types() {
		return new HashSet<>(Arrays.asList("gcs"));
	}

	@Override
	public URI generateUri(String path) throws URISyntaxException {
		return new URI("gcs://" + path);
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

	@Override
	public FileProvider getFileProvider() {
		return provider;
	}

	@Override
	public String getScheme() {
		return "gcs";
	}

}
