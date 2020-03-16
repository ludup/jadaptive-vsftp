package com.jadaptive.plugins.ssh.vsftp.schemes;

import java.io.IOException;

import org.apache.commons.vfs2.FileSystemOptions;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.api.template.EntityTemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderOptions;
import com.sshtools.vfs.sftp.SftpFileProvider;
import com.sshtools.vfs.sftp.SftpFileSystemConfigBuilder;

@Extension
public class SftpFileScheme extends AbstractFileScheme {

	@Autowired
	EntityTemplateService templateService; 
	
	public SftpFileScheme() {
		super("sftp", new SftpFileProvider(), "sftp", "ssh", "scp");
	}
	
	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder folder) throws IOException {
		
		FileSystemOptions opts = new FileSystemOptions();
		SftpCredentials creds = (SftpCredentials) folder.getCredentials();
		
		SftpFileSystemConfigBuilder.getInstance().setPrivateKey(opts, creds.getPrivateKeyCredentials().getPrivateKey());
		SftpFileSystemConfigBuilder.getInstance().setPassphrase(opts, creds.getPrivateKeyCredentials().getPassphrase());
		
		return super.buildFileSystemOptions(folder);
	}

	@Override
	public boolean requiresCredentials() {
		return true;
	}

	@Override
	public EntityTemplate getCredentialsTemplate() {
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
	public EntityTemplate getOptionsTemplate() {
		return templateService.get(SftpOptions.RESOURCE_KEY);
	}

	@Override
	public Class<? extends VirtualFolderOptions> getOptionsClass() {
		return SftpOptions.class;
	}	



}
