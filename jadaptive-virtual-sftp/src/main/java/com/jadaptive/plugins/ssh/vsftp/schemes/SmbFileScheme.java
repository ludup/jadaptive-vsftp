package com.jadaptive.plugins.ssh.vsftp.schemes;

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
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.api.template.EntityTemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Extension
public class SmbFileScheme extends AbstractFileScheme {

	
	@Autowired
	EntityTemplateService templateService; 

	public SmbFileScheme() {
		super("Windows CIFS", new SmbFileProvider(), "smb", "windows", "cifs");
	}

	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder folder) throws IOException {

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
	public boolean requiresCredentials() {
		return true;
	}

	@Override
	public URI generateUri(String path) throws URISyntaxException {
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
	public EntityTemplate getCredentialsTemplate() {
		return templateService.get(WindowsCredentials.RESOURCE_KEY);
	}

	@Override
	public Class<? extends VirtualFolderCredentials> getCredentialsClass() {
		return WindowsCredentials.class;
	}
}
