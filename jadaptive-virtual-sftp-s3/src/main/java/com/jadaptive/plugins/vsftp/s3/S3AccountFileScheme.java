package com.jadaptive.plugins.vsftp.s3;

import java.io.IOException;
import java.net.URISyntaxException;

import org.pf4j.Extension;
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
public class S3AccountFileScheme extends AbstractFileScheme {
	
	public static final String SCHEME_TYPE = "s3Account";
	public static final String RESOURCE_KEY = "s3AccountFolder";
	
	@Autowired
	private TemplateService templateService;

	public S3AccountFileScheme() {
		super(RESOURCE_KEY, "Account S3", SCHEME_TYPE);
	}

	public boolean requiresCredentials() {
		return false;
	}
	
	@Override
	public String getIcon() {
		return "fa-brands fa-aws";
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
	public boolean createRoot() {
		return true;
	}
	
	@Override
	public void configure(VirtualFolder folder) {
	
	}
	
	@Override
	public VirtualFolder createVirtualFolder(String name, String mountPath, VirtualFolderPath path,
			VirtualFolderCredentials creds) {
		
		S3AccountFolder folder = new S3AccountFolder();
		
		S3FolderPath sfxPath = (S3FolderPath) path;
		
		folder.setName(name);
		folder.setMountPath(mountPath);
		folder.setPath(sfxPath);
		
		
		return folder;
	}

	@Override
	public String getBundle() {
		return S3AccountFolder.RESOURCE_KEY;
	}
	
	@Override
	public Integer getWeight() {
		return 0;
	}

	@Override
	protected AbstractFileFactory<?> onConfigureFactory(VirtualFolder folder) throws IOException {
		
		S3FolderPath sfxPath = (S3FolderPath) folder.getPath();
		try {
			return new S3AbstractFileFactory(sfxPath.getRegion().getSDKRegion(),
					sfxPath.getBucket(), true, 1000);
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
}
