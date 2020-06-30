package com.jadaptive.plugins.ssh.vsftp;

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
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.user.User;
import com.sshtools.common.files.vfs.VFSFileFactory;
import com.sshtools.common.files.vfs.VirtualMountTemplate;

@Service
public class VirtualFileServiceImpl extends AuthenticatedService implements VirtualFileService {

	static Logger log = LoggerFactory.getLogger(VirtualFileServiceImpl.class);
	
	@Autowired
	private AssignableObjectDatabase<VirtualFolder> repository;
	
	@Autowired
	private ApplicationService applicationService; 

	
	private Set<String> types;
	private Set<FileScheme> schemes = new HashSet<>();
	private Map<String, FileScheme> providers = new HashMap<>();
	private Map<String, FileSystemManager> managers = new HashMap<>();
	
	@Override
	public Iterable<VirtualFolder> getVirtualFolders() {
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
			for(FileScheme scheme : schemes) {
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
	public FileScheme getFileScheme(String type) {
		if(providers.isEmpty()) {
			checkSupportedMountType(type);
		}
		return providers.get(type);
	}
	
	@Override
	public VirtualFolder createOrUpdate(VirtualFolder folder, Collection<User> users, Collection<Role> roles) {
		
		assertWrite(VirtualFolder.RESOURCE_KEY);
		
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
		
		repository.saveOrUpdate(folder);
		
		return folder;
	}
	
	@Override
	public VFSFileFactory resolveMount(VirtualFolder folder) throws IOException {
		
		try {
			FileScheme scheme = getFileScheme(folder.getType());
			FileSystemOptions opts = scheme.buildFileSystemOptions(folder);
			FileSystemManager mgr = getManager(folder.getUuid(), folder.getCacheStrategy());
			FileObject obj = mgr.resolveFile(
							scheme.generateUri(
									folder.getDestinationUri()).toASCIIString(), opts);
			
			if(!obj.exists()) {
				throw new FileNotFoundException("Destination of mount was not found");
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
				for(FileScheme scheme : schemes) {
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
			for(FileScheme scheme : applicationService.getBeans(FileScheme.class)) {
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
			FileScheme scheme = getFileScheme(folder.getType());
			FileSystemOptions opts = scheme.buildFileSystemOptions(folder);
			FileSystemManager manager = getManager(folder.getUuid(), folder.getCacheStrategy());

			return new VirtualMountTemplate(folder.getMountPath(),
					scheme.generateUri(folder.getDestinationUri()).toASCIIString(),
					new VFSFileFactory(manager, opts), scheme.createRoot());
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	
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
	public Collection<FileScheme> getSchemes() {
		checkSchemes();
		return Collections.unmodifiableCollection(schemes);
	}

	@Override
	public VirtualFolder getHomeMount() {
		return repository.getObject(VirtualFolder.class, getCurrentUser(), SearchField.eq("mount", "/"));
	}

	@Override
	public VirtualFolder getVirtualFolderByShortCode(String shortCode) {
		return repository.getObject(VirtualFolder.class, getCurrentUser(), SearchField.eq("shortCode", shortCode));
	}
}
