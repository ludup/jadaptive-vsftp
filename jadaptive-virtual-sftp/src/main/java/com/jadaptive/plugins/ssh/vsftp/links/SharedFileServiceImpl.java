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

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.app.StartupAware;
import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.db.SingletonObjectDatabase;
import com.jadaptive.api.entity.AbstractUUIDObjectServceImpl;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.events.EventService;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.stats.ResourceService;
import com.jadaptive.api.user.User;
import com.jadaptive.plugins.email.MessageService;
import com.jadaptive.plugins.email.RecipientHolder;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.licensing.FeatureGroup;
import com.jadaptive.plugins.ssh.vsftp.VFSConfiguration;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.ui.TransferResult;
import com.jadaptive.utils.StaticResolver;
import com.jadaptive.utils.Utils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;

@Service
public class SharedFileServiceImpl extends AbstractUUIDObjectServceImpl<SharedFile> implements SharedFileService, ResourceService, StartupAware {

	public static final String SHARED_FILE_DOWNLOAD = "f5c09928-d9af-480c-83a4-93021e0779cb";
	public static final String SHARED_FILE_CREATED = "a6a1fabd-895a-41ab-8944-1847501881d8";
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private SingletonObjectDatabase<VFSConfiguration> configService;
	
	@Autowired
	private EventService eventService; 
	
	@Override
	protected Class<SharedFile> getResourceClass() {
		return SharedFile.class;
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
		try {
			AbstractFile file = fileService.getFile(object.getVirtualPath());
					
			if(file.isDirectory()) {
				object.setFilename(file.getName() + ".zip");
			} else {
				object.setFilename(file.getName());
			}
			
			if(Objects.isNull(object.getSharedBy())) {
				object.setSharedBy(getCurrentUser());
			}
		} catch (IOException | PermissionDeniedException e) {
			throw new ObjectException(e.getMessage(), e);
		}
		
		object.setVirtualPath(FileUtils.checkEndsWithNoSlash(object.getVirtualPath()));

	}

	@Override
	public SharedFile createDownloadLink(AbstractFile file, User user) throws IOException, PermissionDeniedException {
		SharedFile link = new SharedFile();

		link.setVirtualPath(file.getAbsolutePath());
		link.setSharedBy(user);
		if(file.isDirectory()) {
			link.setFilename(file.getName() + ".zip");
		} else {
			link.setFilename(file.getName());
		}
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
	public String getPublicLink(SharedFile share) {
		return Utils.encodeURIPath("/app/ui/download/" + share.getShortCode() + "/" + share.getFilename());
	}

	private long getFileLength(AbstractFile file) {
		try {
			return file.length();
		} catch(PermissionDeniedException | IOException e) {
			return 0L;
		}
	}
	
	@Override
	public void notifyShareAccess(SharedFile share, Date started, AbstractFile file) {
		
		eventService.publishEvent(new ShareDownloadEvent(new TransferResult(
				share.getFilename(), share.getVirtualPath(), getFileLength(file),started, Utils.now())));
		
		StaticResolver resolver = new StaticResolver();
		resolver.addToken("serverName", Request.get().getServerName());
		resolver.addToken("filename", share.getFilename());
		resolver.addToken("share", share);
		resolver.addToken("ipAddress", Request.get().getRemoteAddr());
		resolver.addToken("sharedBy", StringUtils.defaultString(
				share.getSharedBy().getName(),
				share.getSharedBy().getUsername()));
		
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
}
