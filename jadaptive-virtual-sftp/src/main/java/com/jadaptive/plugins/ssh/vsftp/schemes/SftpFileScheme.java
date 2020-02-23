package com.jadaptive.plugins.ssh.vsftp.schemes;

import java.io.IOException;

import org.apache.commons.vfs2.FileSystemOptions;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.api.template.EntityTemplateService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.sshtools.vfs.sftp.SftpFileProvider;

public class SftpFileScheme extends AbstractFileScheme {

	@Autowired
	EntityTemplateService templateService; 
	
	public SftpFileScheme() {
		super("sftp", new SftpFileProvider(), "sftp", "ssh", "scp");
	}
	
	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder folder) throws IOException {
		// TODO Auto-generated method stub
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



}
