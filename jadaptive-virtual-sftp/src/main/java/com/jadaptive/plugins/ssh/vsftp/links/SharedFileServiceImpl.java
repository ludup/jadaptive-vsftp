package com.jadaptive.plugins.ssh.vsftp.links;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.app.StartupAware;
import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.db.SingletonObjectDatabase;
import com.jadaptive.api.entity.AbstractUUIDObjectServceImpl;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.stats.ResourceService;
import com.jadaptive.api.tenant.Tenant;
import com.jadaptive.api.tenant.TenantAware;
import com.jadaptive.api.user.User;
import com.jadaptive.plugins.email.MessageService;
import com.jadaptive.plugins.email.RecipientHolder;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.licensing.FeatureGroup;
import com.jadaptive.plugins.ssh.vsftp.VFSConfiguration;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.utils.StaticResolver;
import com.jadaptive.utils.Utils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.vfs.VirtualFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;

@Service
public class SharedFileServiceImpl extends AbstractUUIDObjectServceImpl<SharedFile> implements SharedFileService, ResourceService, StartupAware, TenantAware {

	public static final String SHARED_FILE_DOWNLOAD = "f5c09928-d9af-480c-83a4-93021e0779cb";
	public static final String SHARED_FILE_CREATED = "a6a1fabd-895a-41ab-8944-1847501881d8";
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private SingletonObjectDatabase<VFSConfiguration> configService;

	
	private boolean inStartup = false;

	@Override
	protected Class<SharedFile> getResourceClass() {
		return SharedFile.class;
	}

	@Override
	public void initializeSystem(boolean newSchema) {
		initializeTenant(getCurrentTenant(), newSchema);
	}

	@Override
	public void initializeTenant(Tenant tenant, boolean newSchema) {
		if(!newSchema) {
			inStartup = true;
			try {
				for(SharedFile file : allObjects()) {
					if(file.getVirtualPaths().isEmpty()) {
						Collection<String> tmp = new ArrayList<>();
						tmp.add(file.getVirtualPath());
						file.setVirtualPaths(tmp);
					}
					if(StringUtils.isBlank(file.getName())) {
						file.setName(file.getFilename());	
					}
					file.setOwnerUUID(file.getSharedBy().getUuid());
					saveOrUpdate(file);
				}
			} finally {
				inStartup = false;
			}
		}
	}

	
	public SharedFile getDownloadByPath(String path, User user) {
		return objectDatabase.get(getResourceClass(), 
				SearchField.eq("virtualPath", FileUtils.checkEndsWithNoSlash(path)),
				SearchField.eq("sharedBy", user.getUuid()));
	}
	
	@Override
	protected void validateSave(SharedFile object) {
		if(StringUtils.isBlank(object.getShortCode())) {
			object.setShortCode(Utils.generateRandomAlphaNumericString(8));
		}
		
		if(inStartup) {
			return;
		}
		
		try {
			
			if(object.getVirtualPaths().size()==1) {
				validateSingleFile(object);
			} else {
				validateMultipleFiles(object);
			}
			
			if(Objects.isNull(object.getSharedBy())) {
				object.setSharedBy(getCurrentUser());
			}
		} catch (IOException | PermissionDeniedException e) {
			throw new ObjectException(e.getMessage(), e);
		}

	}

	private VirtualFile validateFile(String virtualPath) throws PermissionDeniedException, IOException {
		VirtualFile file = (VirtualFile) fileService.getFile(virtualPath);
		if(!file.exists()) {
			throw new IOException(virtualPath + " does not exist!");
		}
		VirtualFolder mount = fileService.getVirtualFolder(file.getMount().getMount());
		if(file.isFile() && !mount.getShareFiles()) {
			throw new PermissionDeniedException("You do not have the right to share this file");
		}
		if(file.isDirectory() && !mount.getShareFolders()) {
			throw new PermissionDeniedException("You do not have the right to share this folder");	
		}
		return file;
	}
	
	private void validateMultipleFiles(SharedFile object) throws PermissionDeniedException, IOException {
		
		for(String virtualPath : object.getVirtualPaths()) {
			validateFile(virtualPath);
		}

		if(StringUtils.isBlank(object.getFilename())) {
			object.setFilename(object.getName() + ".zip");
		}
	}

	private void validateSingleFile(SharedFile object) throws PermissionDeniedException, IOException {

		String virtualPath = object.getVirtualPaths().iterator().next();
		AbstractFile file = validateFile(virtualPath);
		
		if(StringUtils.isBlank(object.getFilename())) {
			if(file.isDirectory()) {
				object.setFilename(file.getName() + ".zip");
			} else {
				object.setFilename(file.getName());
			}
		}
	}

	@Override
	public SharedFile createDownloadLink(AbstractFile file, User user) throws IOException, PermissionDeniedException {
		SharedFile link = new SharedFile();

		List<String> tmp = new ArrayList<>();
		tmp.add(file.getAbsolutePath());
		link.setVirtualPaths(tmp);
		link.setSharedBy(user);
		link.setName(file.getName());
		
		saveOrUpdate(link);
		
		notifyShareCreation(link);
		
		return link;
	}

	@Override
	public SharedFile getDownloadByShortCode(String shortCode) {
		return objectDatabase.get(getResourceClass(), SearchField.eq("shortCode", shortCode));
	}

	@Override
	public String getDirectLink(SharedFile share) {
		return Utils.encodeURIPath("/app/vfs/downloadLink/" + share.getShortCode() + "/" + share.getFilename());
	}
	
	@Override
	public String getDirectLink(SharedFile share, String virtualPath) {
		return Utils.encodeURIPath("/app/vfs/downloadPart/" + share.getShortCode()
				+ "/" + DigestUtils.md5DigestAsHex(Utils.getUTF8Bytes(virtualPath)).substring(0, 8)
				+ "/" + Utils.urlEncode(FileUtils.getFilename(virtualPath)));
	}

	@Override
	public String getPublicLink(SharedFile share) {
		return Utils.encodeURIPath("/app/ui/download/" + share.getShortCode() + "/" + share.getFilename());
	}

	@Override
	public void notifyShareAccess(SharedFile share, Date started, String... paths) {
		
//		eventService.publishEvent(new ShareDownloadEvent(new TransferResult(
//				share.getFilename(), share.getVirtualPath(), getFileLength(file),started, Utils.now())));
//		
		StaticResolver resolver = new StaticResolver();
		resolver.addToken("serverName", Request.get().getServerName());
		resolver.addToken("filename", share.getFilename());
		resolver.addToken("share", share);
		resolver.addToken("ipAddress", Request.getRemoteAddress());
		resolver.addToken("sharedBy", StringUtils.defaultString(
				share.getSharedBy().getName(),
				share.getSharedBy().getUsername()));
		resolver.addToken("virtualPaths", paths);
		
		List<RecipientHolder> emailAddresses =  new ArrayList<>();
		emailAddresses.add(new RecipientHolder(share.getSharedBy()));
		
		for(String email : share.getOtherEmails()) {
			emailAddresses.add(new RecipientHolder(email));
		}
		
		VFSConfiguration config = configService.getObject(VFSConfiguration.class);
		
		for(String email : config.getNotificationEmails()) {
			emailAddresses.add(new RecipientHolder(email));
		}
		
		
		MessageService messageService = applicationService.getBean(MessageService.class);
		
		messageService.sendMessage(SHARED_FILE_DOWNLOAD, resolver, emailAddresses);
		
	}

	@Override
	public void notifyShareCreation(SharedFile share) {
		
		StaticResolver resolver = new StaticResolver();
		resolver.addToken("serverName", Request.get().getServerName());
		resolver.addToken("filename", share.getFilename());
		resolver.addToken("share", share);
		resolver.addToken("url", String.format("https://%s/app/ui/share/%s", Request.get().getServerName(), share.getShortCode()));
		resolver.addToken("sharedBy", StringUtils.defaultString(
				share.getSharedBy().getName(),
				share.getSharedBy().getUsername()));
		
		List<RecipientHolder> emailAddresses =  new ArrayList<>();
		emailAddresses.add(new RecipientHolder(share.getSharedBy()));
		
		Collection<String> tmp = share.getOtherEmails();
		if(Objects.nonNull(tmp)) {
			for(String email : tmp) {
				emailAddresses.add(new RecipientHolder(email));
			}
		}
		
		VFSConfiguration config = configService.getObject(VFSConfiguration.class);
		
		tmp = config.getNotificationEmails();
		if(Objects.nonNull(tmp)) {
			for(String email : tmp) {
				emailAddresses.add(new RecipientHolder(email));
			}
		}
		
		MessageService messageService = applicationService.getBean(MessageService.class);
		
		messageService.sendMessage(SHARED_FILE_CREATED, resolver, emailAddresses);
	}

	@Override
	public boolean isEnabled() {
		return applicationService.getBean(FeatureEnablementService.class).isEnabled(SHARING);
	}

	@Override
	public String getResourceKey() {
		return SharedFile.RESOURCE_KEY;
	}

	@Override
	public long getTotalResources() {
		return objectDatabase.count(SharedFile.class);
	}

	@Override
	public void onApplicationStartup() {	
		applicationService.getBean(FeatureEnablementService.class).registerFeature(SHARING, FeatureGroup.PROFESSIONAL);
	}

	@Override
	public Iterable<SharedFile> getUserShares() {
		return objectDatabase.list(getResourceClass(), SearchField.eq("sharedBy", getCurrentUser().getUuid()));
	}
}
