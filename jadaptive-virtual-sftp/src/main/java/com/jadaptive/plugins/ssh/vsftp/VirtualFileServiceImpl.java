package com.jadaptive.plugins.ssh.vsftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.FileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.db.AssignableObjectDatabase;
import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.ui.PageCache;
import com.jadaptive.api.user.User;
import com.jadaptive.plugins.ssh.vsftp.ui.Tree;
import com.sshtools.common.files.vfs.VFSFileFactory;
import com.sshtools.common.files.vfs.VirtualMountTemplate;
import com.sshtools.common.util.Utils;

@Service
public class VirtualFileServiceImpl extends AuthenticatedService implements VirtualFileService {

	static Logger log = LoggerFactory.getLogger(VirtualFileServiceImpl.class);
	
	@Autowired
	private AssignableObjectDatabase<VirtualFolder> repository;
	
	@Autowired
	private ApplicationService applicationService; 

	@Autowired
	private PageCache pageCache; 
	
	private Set<String> types;
	private Set<FileScheme<?>> schemes = new HashSet<>();
	private Map<String, FileScheme<?>> providers = new HashMap<>();
	private Map<String, FileSystemManager> managers = new HashMap<>();
	
	@PostConstruct
	private void postConstruct() {
		pageCache.setHomePage(Tree.class);
	}
	
	@Override
	public Iterable<VirtualFolder> allObjects() {
		return repository.getObjects(
				VirtualFolder.class);
	}
	
	@Override
	public Iterable<VirtualFolder> getPersonalFolders() {
		return repository.getAssignedObjects(
				VirtualFolder.class, 
				getCurrentUser());
	}

	@Override
	public boolean checkMountExists(String mount, User user) {
		
		try {
			repository.getObjectByUUID(VirtualFolder.class, mount);
			return true;
		} catch(ObjectNotFoundException e) { } 
		return false;
	}

	@Override
	public boolean checkSupportedMountType(String type) {
		
		if(Objects.isNull(types)) {
			types = new HashSet<>();
			checkSchemes();
			for(FileScheme<?> scheme : schemes) {
				types.addAll(scheme.types());
				for(String t : scheme.types()) {
					log.info("Registering file scheme {}", t);
					providers.put(t, scheme);
				}
			}
		}
		return types.contains(type.toLowerCase());
	}

	@Override
	public FileScheme<?> getFileScheme(String type) {
		if(providers.isEmpty()) {
			checkSupportedMountType(type);
		}
		return providers.get(type);
	}
	
	@Override
	public VirtualFolder createOrUpdate(VirtualFolder folder, Collection<User> users, Collection<Role> roles) {
		
		assertWrite(VirtualFolder.RESOURCE_KEY);
		
		try {
			resolveMount(folder);
		} catch(IOException e) {
			throw new ObjectException(String.format("Cannot resolve folder %s", folder.getName()), e);
		}
		if(StringUtils.isBlank(folder.getShortCode())) {
			folder.setShortCode(Utils.randomAlphaNumericString(16));
		}
		
		folder.getRoles().clear();
		folder.getUsers().clear();
		for(Role role : roles) {
			folder.getRoles().add(role.getUuid());
		}
		for(User user : users) {
			folder.getUsers().add(user.getUuid());
		}
		
		repository.saveOrUpdate(folder);
		
		return folder;
	}
	
	@Override
	public VirtualFolder createOrUpdate(VirtualFolder folder) {
		
		assertWrite(VirtualFolder.RESOURCE_KEY);
		
		try {
			resolveMount(folder);
		} catch(IOException e) {
			throw new ObjectException(String.format("Cannot resolve folder %s", folder.getName()), e);
		}
		if(StringUtils.isBlank(folder.getShortCode())) {
			folder.setShortCode(Utils.randomAlphaNumericString(16));
		}
		repository.saveOrUpdate(folder);
		
		return folder;
	}
	
	@Override
	public VFSFileFactory resolveMount(VirtualFolder folder) throws IOException {
		
		try {
			FileScheme<?> scheme = getFileScheme(folder.getType());
			FileSystemOptions opts = scheme.buildFileSystemOptions(folder);
			FileSystemManager mgr = getManager(folder.getUuid(), folder.getPath().getCacheStrategy());
			FileObject obj = mgr.resolveFile(
							scheme.generateUri(replaceVariables(folder.getPath().generatePath())).toASCIIString(), opts);
			
			if(!obj.exists() && scheme.createRoot()) {
				obj.createFolder();
			}
			
			if(!obj.exists()) {
				throw new FileNotFoundException("Destination of mount does not exist");
			}
			
			return new VFSFileFactory(mgr, opts);
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
	
	public FileSystemManager getManager(String id, CacheStrategy cacheStrategy)
			throws FileSystemException {
		
		checkSchemes();
		
		synchronized (managers) {
			String key = id == null ? "__DEFAULT__" : id;
			FileSystemManager mgr = managers.get(key);
			if (mgr == null) {
				DefaultFileSystemManager vfsMgr = new DefaultFileSystemManager();
				vfsMgr.setLogger(LogFactory.getLog(key));
				vfsMgr.setCacheStrategy(cacheStrategy);
				for(FileScheme<?> scheme : schemes) {
					if(!vfsMgr.hasProvider(scheme.getScheme())) {
						log.info("Registering {} file scheme", scheme.getScheme());
						vfsMgr.addProvider(scheme.getScheme(), scheme.getFileProvider());
					}
				}
				vfsMgr.init();
				managers.put(key, vfsMgr);
				mgr = vfsMgr;
			}
			return mgr;
		}
	}
	
	private void checkSchemes() {
		
		if(schemes.isEmpty()) {
			for(FileScheme<?> scheme : applicationService.getBeans(FileScheme.class)) {
				schemes.add(scheme);
			}
		}
		
	}

	@Override
	public void addProvider(String scheme, FileProvider provider) throws FileSystemException {
		synchronized (managers) {
			if (providers.containsKey(scheme))
				throw new IllegalArgumentException(String.format("Provider already registered for %s.", scheme));
			for (Map.Entry<String, FileSystemManager> en : managers.entrySet()) {
				if (en.getValue() instanceof DefaultFileSystemManager) {
					((DefaultFileSystemManager) en.getValue()).addProvider(scheme, provider);
				}
			}
		}

	}

	@Override
	public VirtualMountTemplate getVirtualMountTemplate(VirtualFolder folder) throws IOException {
		
		try {
			FileScheme<?> scheme = getFileScheme(folder.getType());
			FileSystemOptions opts = scheme.buildFileSystemOptions(folder);
			FileSystemManager manager = getManager(folder.getUuid(), folder.getPath().getCacheStrategy());

			return new VirtualFolderMount(folder,
					scheme.generateUri(replaceVariables(folder.getPath().generatePath())).toASCIIString(),
					new VFSFileFactory(manager, opts), scheme.createRoot());
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	
	}

	private String replaceVariables(String destinationUri) {
		return destinationUri.replace("%USERNAME%", getCurrentUser().getUsername())
				.replace("%INSTALLPATH%", new File(".").getAbsolutePath());
	}

	@Override
	public VirtualFolder getVirtualFolder(String mount) {
		return repository.getObjectByUUID(VirtualFolder.class, mount);
	}

	@Override
	public void deleteVirtualFolder(VirtualFolder virtualFolder) {
		assertWrite(VirtualFolder.RESOURCE_KEY);
		repository.deleteObject(virtualFolder);
	}

	@Override
	public Collection<FileScheme<?>> getSchemes() {
		checkSchemes();
		return Collections.<FileScheme<?>>unmodifiableCollection(schemes);
	}

	@Override
	public VirtualFolder getHomeMount(User user) {
		return repository.getAssignedObject(VirtualFolder.class, user, SearchField.eq("mountPath", "/"));
	}

	@Override
	public VirtualFolder getVirtualFolderByShortCode(String shortCode) {
		return repository.getAssignedObject(VirtualFolder.class, getCurrentUser(), SearchField.eq("shortCode", shortCode));
	}

	@Override
	public VirtualFolder getObjectByUUID(String uuid) {
		return repository.getObjectByUUID(VirtualFolder.class, uuid);
	}

	@Override
	public String saveOrUpdate(VirtualFolder folder) {
		createOrUpdate(folder);
		return folder.getUuid();
	}

	@Override
	public void deleteObject(VirtualFolder object) {
		deleteVirtualFolder(object);
	}

	@Override
	public long getTotalResources() {
		return repository.countObjects(VirtualFolder.class);
	}

	@Override
	public String getResourceKey() {
		return VirtualFolder.RESOURCE_KEY;
	}

	@Override
	public void deleteObjectByUUID(String uuid) {
		deleteObject(getObjectByUUID(uuid));
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
