package com.jadaptive.plugins.vsftp.s3;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.FileProvider;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.api.template.EntityTemplateService;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.sshtools.vfs.s3.provider.s3.S3FileProvider;

@Extension
public class S3FileScheme implements FileScheme {

	S3FileProvider provider = new S3FileProvider();
	
	static Logger log = LoggerFactory.getLogger(S3FileScheme.class);
	
	@Autowired
	private EntityTemplateService templateService; 
	
	public FileSystemOptions buildFileSystemOptions(VirtualFolder folder) throws IOException {
		
		FileSystemOptions opts = new FileSystemOptions();
		
		if(Objects.nonNull(folder.getCredentials()) && folder.getCredentials() instanceof S3Credentials) {
	        try {
	        	
	        	S3Credentials credentials = (S3Credentials) folder.getCredentials();
	            DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, 
	            		new StaticUserAuthenticator(null, credentials.getAccessKey(), 
	            				credentials.getSecretKey()));

	        } catch (FileSystemException e) {
	            log.error(String.format("Failed to set credentials on %s", folder.getMountPath()));
	        }
		}

        return opts;
	}

	public boolean requiresCredentials() {
		return true;
	}

	public Set<String> types() {
		return new HashSet<>(Arrays.asList("s3", "aws", "amazon"));
	}

	public URI generateUri(String path) throws URISyntaxException {
		return new URI("s3:///" + path);
	}

	public FileProvider getFileProvider() {
		return provider;
	}

	public EntityTemplate getCredentialsTemplate() {
		return templateService.get("s3Credentials");
	}

	public Class<? extends VirtualFolderCredentials> getCredentialsClass() {
		return S3Credentials.class;
	}

	public String getScheme() {
		return "s3";
	}

}
