package com.jadaptive.plugins.vsftp.s3;

import java.io.IOException;
import java.net.URISyntaxException;

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
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.synergy.s3.S3AbstractFileFactory;

@Extension
public class S3FileScheme extends AbstractFileScheme {
	
	static Logger log = LoggerFactory.getLogger(S3FileScheme.class);
	
	public static final String SCHEME_TYPE = "s3";
	public static final String RESOURCE_KEY = "s3";
	
	@Autowired
	private TemplateService templateService; 
	
	public S3FileScheme() {
		super(RESOURCE_KEY, "Plan Storage", SCHEME_TYPE);
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

	@Override
	public AbstractFileFactory<?> configureFactory(VirtualFolder folder) throws IOException {
		
		S3Folder s3 = (S3Folder) folder;
		S3FolderPath sfxPath = (S3FolderPath) folder.getPath();
		try {
			return new S3AbstractFileFactory(sfxPath.getRegion(),
					s3.getCredentials().getAccessKey(),
					s3.getCredentials().getSecretKey(),
					sfxPath.getBucket());
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
}
