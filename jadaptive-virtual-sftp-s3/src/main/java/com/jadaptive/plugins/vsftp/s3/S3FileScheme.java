package com.jadaptive.plugins.vsftp.s3;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolderPath;
import com.jadaptive.plugins.ssh.vsftp.schemes.AbstractFileScheme;
import com.sshtools.vfs.s3.provider.s3.S3FileProvider;

@Extension
public class S3FileScheme extends AbstractFileScheme<S3FileProvider> {
	
	public static final String SCHEME_TYPE = "s3";
	
	@Autowired
	private TemplateService templateService; 
	
	public S3FileScheme() {
		super("Amazon S3", new S3FileProvider(), "s3", "aws", "amazon", S3Folder.RESOURCE_KEY);
	}
	
	public FileSystemOptions buildFileSystemOptions(VirtualFolder vf) throws IOException {
		
		S3Folder folder = (S3Folder)vf;
		FileSystemOptions opts = new FileSystemOptions();
		
		if(Objects.nonNull(folder.getCredentials()) && folder.getCredentials() instanceof S3Credentials) {
	        try {
	        	
	        	S3Credentials credentials = (S3Credentials) folder.getCredentials();
	            DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, 
	            		new StaticUserAuthenticator(null, credentials.getAccessKey(), 
	            				decryptCredentials(credentials.getSecretKey())));

	        } catch (FileSystemException e) {
	            log.error(String.format("Failed to set credentials on %s", folder.getMountPath()));
	        }
		}

        return opts;
	}

	public boolean requiresCredentials() {
		return true;
	}

	public ObjectTemplate getCredentialsTemplate() {
		return templateService.get("s3Credentials");
	}

	public Class<? extends VirtualFolderCredentials> getCredentialsClass() {
		return S3Credentials.class;
	}
	
	@Override
	public String getIcon() {
		return "fab fa-aws";
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
