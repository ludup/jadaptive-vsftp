package com.jadaptive.plugins.ssh.vsftp.schemes;

import java.io.IOException;

import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderOptions;
import com.jadaptive.plugins.ssh.vsftp.folders.SftpFolder;
import com.sshtools.vfs.sftp.SftpFileProvider;
import com.sshtools.vfs.sftp.SftpFileSystemConfigBuilder;

@Extension
public class SftpFileScheme extends AbstractFileScheme<SftpFileProvider> {

	public static final String SCHEME_TYPE = "sftp";
	
	@Autowired
	TemplateService templateService; 
	
	public SftpFileScheme() {
		super("sftp", new SftpFileProvider(), "sftp", "ssh", "scp");
	}
	
	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder vf) throws IOException {
		
		SftpFolder folder = (SftpFolder) vf;
		FileSystemOptions opts = new FileSystemOptions();
		SftpCredentials creds = folder.getCredentials();
		
		StaticUserAuthenticator auth = new StaticUserAuthenticator("", 
				creds.getBasicCredentials().getUsername(), 
				creds.getBasicCredentials().getPassword());
		
		SftpFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
		SftpFileSystemConfigBuilder.getInstance().setPrivateKey(opts, creds.getPrivateKeyCredentials().getPrivateKey());
		SftpFileSystemConfigBuilder.getInstance().setPassphrase(opts, creds.getPrivateKeyCredentials().getPassphrase());

		return opts;
	}

	@Override
	public boolean requiresCredentials() {
		return true;
	}

	@Override
	public ObjectTemplate getCredentialsTemplate() {
		return templateService.get(SftpCredentials.RESOURCE_KEY);
	}

	@Override
	public Class<? extends VirtualFolderCredentials> getCredentialsClass() {
		return SftpCredentials.class;
	}
	
	@Override
	public boolean hasExtendedOptions() {
		return true;
	}

	@Override
	public ObjectTemplate getOptionsTemplate() {
		return templateService.get(SftpOptions.RESOURCE_KEY);
	}

	@Override
	public Class<? extends VirtualFolderOptions> getOptionsClass() {
		return SftpOptions.class;
	}	

	@Override
	public VirtualFolder createFolder() {
		return new SftpFolder();
	}

	@Override
	public void setCredentials(VirtualFolder folder, VirtualFolderCredentials credentials) {
		((SftpFolder)folder).setCredentials((SftpCredentials) credentials);
	}

	@Override
	public void setOptions(VirtualFolder folder, VirtualFolderOptions options) {
		((SftpFolder)folder).setOptions((SftpOptions) options);
	}

	@Override
	public String getIcon() {
		return "far fa-terminal";
	}
}
