package com.jadaptive.plugins.vsftp.s3;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.api.template.EntityTemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.schemes.AbstractFileScheme;
import com.sshtools.vfs.s3.provider.s3.S3FileProvider;

@Extension
public class S3FileScheme extends AbstractFileScheme {
	
	@Autowired
	private EntityTemplateService templateService; 
	
	public S3FileScheme() {
		super("Amazon S3", new S3FileProvider(), "s3", "aws", "amazon");
	}
	
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

	public EntityTemplate getCredentialsTemplate() {
		return templateService.get("s3Credentials");
	}

	public Class<? extends VirtualFolderCredentials> getCredentialsClass() {
		return S3Credentials.class;
	}
}
