package com.jadaptive.plugins.vsftp.dropbox;

import java.io.IOException;
import java.util.Objects;

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
import com.jadaptive.plugins.ssh.vsftp.schemes.VFSFileScheme;
import com.sshtools.vfs.dropbox.DropboxFileProvider;

@Extension
public class DropboxFileScheme extends VFSFileScheme<DropboxFileProvider> {

	public static final String SCHEME_TYPE = "dropbox";
	
	@Autowired
	private TemplateService templateService; 
	
	public DropboxFileScheme() {
		super(DropboxFolder.RESOURCE_KEY, "Dropbox", new DropboxFileProvider(), "dropbox");
	}
	
	@Override
	public boolean requiresCredentials() {
		return true;
	}
	
	@Override
	public boolean createRoot() {
		return false;
	}

	@Override
	public ObjectTemplate getCredentialsTemplate() {
		return templateService.get("dropboxCredentials");
	}

	@Override
	public Class<? extends VirtualFolderCredentials> getCredentialsClass() {
		return DropboxCredentials.class;
	}

	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder vf) throws IOException {
		
		DropboxFolder folder = (DropboxFolder) vf;
		FileSystemOptions options = new FileSystemOptions();
		if(Objects.nonNull(folder.getCredentials()) && folder.getCredentials() instanceof DropboxCredentials) {
			
			DropboxCredentials credentials = folder.getCredentials();
			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(options, 
            		new StaticUserAuthenticator(null, credentials.getAccessKey(), 
            				credentials.getSecretKey()));
		}
		return options;
	}

	@Override
	public String getIcon() {
		return "fab fa-dropbox";
	}
	
	@Override
	public ObjectTemplate getPathTemplate() {
		return templateService.get(DropboxFolderPath.RESOURCE_KEY);
	}

	@Override
	public Class<? extends VirtualFolderPath> getPathClass() {
		return DropboxFolderPath.class;
	}

	@Override
	public VirtualFolder createVirtualFolder(String name, String mountPath, VirtualFolderPath path,
			VirtualFolderCredentials creds) {
		
		DropboxFolder folder = new DropboxFolder();
		folder.setName(name);
		folder.setMountPath(mountPath);
		folder.setPath(path);
		folder.setCredentials((DropboxCredentials) creds);
		
		return folder;
	}

	@Override
	public String getBundle() {
		return DropboxFolder.RESOURCE_KEY;
	}
	
	@Override
	public Integer getWeight() {
		return 3000;
	}
}
