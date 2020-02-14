package com.jadaptive.plugins.ssh.vsftp.schemes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.FileProvider;
import org.apache.commons.vfs2.provider.smb.SmbFileProvider;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.api.template.EntityTemplateRepository;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@Extension
public class SmbFileScheme implements FileScheme {

	static Logger log = LoggerFactory.getLogger(SmbFileScheme.class);
	
	SmbFileProvider provider = new SmbFileProvider();
	
	@Autowired
	EntityTemplateRepository templateRepository; 
	
	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder folder) throws IOException {

		String domain = null;
		String username = null;
		String password = null;
		
		StaticUserAuthenticator auth = new StaticUserAuthenticator(domain, username, password);
		
        FileSystemOptions opts = new FileSystemOptions();
        try {
			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
		} catch (FileSystemException e) {
			log.error("Could not set credentials on file system options", e);
		}
		return opts;
	}

	@Override
	public boolean requiresCredentials() {
		return true;
	}

	@Override
	public Set<String> types() {
		return new HashSet<>(Arrays.asList("smb", "windows", "cifs"));
	}

	@Override
	public URI generateUri(String path) throws URISyntaxException {
		return new URI("smb://" + convertWindowsUNCPath(path));
	}

	private String convertWindowsUNCPath(String path) {
		if(path.startsWith("\\\\")) {
			return path.substring(2).replace("\\", "/");
		}
		return path;
	}

	@Override
	public FileProvider getFileProvider() {
		return provider;
	}

	@Override
	public EntityTemplate getCredentialsTemplate() {
		return templateRepository.get(WindowsCredentials.RESOURCE_KEY);
	}
}
