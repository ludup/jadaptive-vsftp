package com.jadaptive.plugins.vsftp.dropbox;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticationData;
import org.apache.commons.vfs2.UserAuthenticationData.Type;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.PropertyService;
import com.jadaptive.api.db.SingletonObjectDatabase;
import com.jadaptive.api.encrypt.EncryptionService;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.entity.ObjectService;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.api.template.ValidationException;
import com.jadaptive.api.ui.UriRedirect;
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
	
	@Autowired
	private ObjectService objectService; 
	
	@Autowired
	private PropertyService propertyService; 
	
	@Autowired
	private EncryptionService encryptionService; 
	
	@Autowired
	private SingletonObjectDatabase<DropboxConfiguration> configDatabase;
	
	public DropboxFileScheme() {
		super(DropboxFolder.RESOURCE_KEY, "Dropbox", new DropboxFileProvider(), "dropbox");
	}
	
	private DropboxConfiguration getConfig() {
		return configDatabase.getObject(DropboxConfiguration.class);
	}
	
	@Override
	public boolean requiresCredentials() {
		return !getConfig().getEnableOauth();
	}
	
	@Override
	public boolean createRoot() {
		return false;
	}

	@Override
	public ObjectTemplate getCredentialsTemplate() {
		if(requiresCredentials()) {
			return templateService.get("dropboxCredentials");
		} else {
			return null;
		}
	}

	@Override
	public Class<? extends VirtualFolderCredentials> getCredentialsClass() {
		if(requiresCredentials()) {
			return DropboxCredentials.class;
		} else {
			return null;
		}
	}
	
	

	@Override
	public void configure(VirtualFolder folder) {
		
		DropboxCredentials credentials = ((DropboxFolder)folder).getCredentials();
		DropboxConfiguration config = getConfig();
		if(config.getEnableOauth() && StringUtils.isBlank(credentials.getAccessKey())) {
			try {
				objectService.stashObject(folder);
				throw new UriRedirect("/app/dropbox/start");
			} catch (ValidationException | RepositoryException | ObjectException | IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}

	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder vf) throws IOException {
		
		FileSystemOptions options = new FileSystemOptions();
		DropboxCredentials credentials = ((DropboxFolder)vf).getCredentials();
		
		DropboxConfiguration config = getConfig();
		if(config.getEnableOauth()) {
			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(options, 
				new UserAuthenticator() {
					
					@Override
					public UserAuthenticationData requestAuthentication(Type[] types) {
						UserAuthenticationData ua = new UserAuthenticationData();
						ua.setData(UserAuthenticationData.DOMAIN, 
								encryptionService.decrypt(config.getAppKey()).toCharArray());
						ua.setData(UserAuthenticationData.USERNAME, 
								encryptionService.decrypt(credentials.getRefreshKey()).toCharArray());
						ua.setData(UserAuthenticationData.PASSWORD, 
								encryptionService.decrypt(credentials.getAccessKey()).toCharArray());
						return ua;
					}
				});
		} else {
			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(options, 
					new UserAuthenticator() {
						@Override
						public UserAuthenticationData requestAuthentication(Type[] types) {
							UserAuthenticationData ua = new UserAuthenticationData();
							ua.setData(UserAuthenticationData.PASSWORD, 
									encryptionService.decrypt(credentials.getAccessKey()).toCharArray());
							return ua;
						}
					});
		}
		return options;
	}

	@Override
	public String getIcon() {
		return "fa-brands fa-dropbox";
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
		
		DropboxCredentials credentials = (DropboxCredentials) creds;
		DropboxFolder folder = new DropboxFolder();
		folder.setUuid(UUID.randomUUID().toString());
		folder.setName(name);
		folder.setMountPath(mountPath);
		folder.setPath(path);
		folder.setCredentials(credentials);
		
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
