package com.jadaptive.plugins.vsftp.azure;

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
import com.jadaptive.plugins.ssh.vsftp.schemes.AbstractFileScheme;
import com.jadaptive.plugins.ssh.vsftp.schemes.BasicCredentials;
import com.sshtools.vfs.azure.AzureFileProvider;

@Extension
public class AzureFileScheme extends AbstractFileScheme<AzureFileProvider> {
	
	public static final String SCHEME_TYPE = "azure";
	
	@Autowired
	private TemplateService templateService; 
	
	public AzureFileScheme() {
		super("Azure", new AzureFileProvider(), "azure", AzureFolder.RESOURCE_KEY);
	}
	
	public FileSystemOptions buildFileSystemOptions(VirtualFolder vf) throws IOException {
		
		AzureFolder folder = (AzureFolder)vf;
		FileSystemOptions opts = new FileSystemOptions();
		
		if(Objects.nonNull(folder.getCredentials()) && folder.getCredentials() instanceof BasicCredentials) {
	        try {
	        	
	        	BasicCredentials credentials = (BasicCredentials) folder.getCredentials();
	            DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, 
	            		new StaticUserAuthenticator(null, credentials.getUsername(), 
	            				credentials.getPassword()));

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
		return BasicCredentials.class;
	}
	
	@Override
	public String getIcon() {
		return "fab fa-azure";
	}

	@Override
	public ObjectTemplate getPathTemplate() {
		return templateService.get(AzureFolderPath.RESOURCE_KEY);
	}

	@Override
	public Class<? extends VirtualFolderPath> getPathClass() {
		return AzureFolderPath.class;
	}
	
	@Override
	public VirtualFolder createVirtualFolder(String name, String mountPath, VirtualFolderPath path,
			VirtualFolderCredentials creds) {
		
		AzureFolder folder = new AzureFolder();
		folder.setName(name);
		folder.setMountPath(mountPath);
		folder.setPath(path);
		folder.setCredentials((BasicCredentials) creds);
		
		return folder;
	}
}
