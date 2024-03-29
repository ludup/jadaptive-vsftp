package com.jadaptive.plugins.vsftp.s3;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.schemes.AbstractFileScheme;
import com.sshtools.vfs.s3.provider.s3.S3FileProvider;

@Extension
public class S3FileScheme extends AbstractFileScheme<S3FileProvider> {
	
	static Logger log = LoggerFactory.getLogger(S3FileScheme.class);
	
	public static final String SCHEME_TYPE = "s3";
	public static final String RESOURCE_KEY = "s3";
	
	@Autowired
	private TemplateService templateService; 
	
	public S3FileScheme() {
		super(S3Folder.RESOURCE_KEY, "Amazon S3", new S3FileProvider(), "s3", "aws", "amazon");
	}
	
	public FileSystemOptions buildFileSystemOptions(VirtualFolder vf) throws IOException {
		
		S3Folder folder = (S3Folder)vf;
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
		return templateService.get(S3FolderPath.RESOURCE_KEY);
	}

	@Override
	public Class<? extends VirtualFolderPath> getPathClass() {
		return S3FolderPath.class;
	}
	
	@Override
	public VirtualFolder createVirtualFolder(String name, String mountPath, VirtualFolderPath path,
			VirtualFolderCredentials creds) {
		
		S3Folder folder = new S3Folder();
		folder.setName(name);
		folder.setMountPath(mountPath);
		folder.setPath(path);
		folder.setCredentials((S3Credentials) creds);
		
		return folder;
	}

	@Override
	public String getBundle() {
		return S3Folder.RESOURCE_KEY;
	}
	
	@Override
	public Integer getWeight() {
		return 2000;
	}
}
