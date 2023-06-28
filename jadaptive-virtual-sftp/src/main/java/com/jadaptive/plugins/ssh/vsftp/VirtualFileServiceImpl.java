package com.jadaptive.plugins.ssh.vsftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.FileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.app.StartupAware;
import com.jadaptive.api.cache.CacheService;
import com.jadaptive.api.db.AssignableObjectDatabase;
import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.entity.AbstractUUIDObjectServceImpl;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.events.EventService;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.user.User;
import com.jadaptive.plugins.ssh.vsftp.pgp.EncryptingFileFactory;
import com.jadaptive.plugins.ssh.vsftp.pgp.PGPEncryptionExtension;
import com.jadaptive.plugins.ssh.vsftp.schemes.VFSFileScheme;
import com.jadaptive.plugins.sshd.SSHDService;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.files.vfs.VirtualFile;
import com.sshtools.common.files.vfs.VirtualMountTemplate;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;

@Service
public class VirtualFileServiceImpl extends AbstractUUIDObjectServceImpl<VirtualFolder> implements VirtualFileService, StartupAware {

	static Logger log = LoggerFactory.getLogger(VirtualFileServiceImpl.class);
	
	private static final String ABSTRACT_FILE_FACTORY = "abstractFileFactory";
	
	@Autowired
	private AssignableObjectDatabase<VirtualFolder> repository;
	
	@Autowired
	private ApplicationService applicationService; 
	
	@Autowired
	private SSHDService sshdService;
	
	@Autowired
	private CacheService cacheService; 
	
	@Autowired
	private EventService eventService; 
	
	private Set<FileScheme> schemes = new HashSet<>();
	private Map<String, FileScheme> providers = new HashMap<>();
	private Map<String, FileSystemManager> managers = new HashMap<>();
	
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
	public void assertSupportedMountType(String type) throws IOException {
		
		checkSchemes();
		FileScheme scheme = providers.get(type);
		
		if(Objects.isNull(scheme)) {
			throw new FileNotFoundException(String.format("%s is not a supported file type", type));
		}
		
		if(!scheme.isEnabled()) {
			throw new FileNotFoundException(String.format("%s is not enabled", scheme.getName()));
		}
	}
	
	@Override
	public FileScheme getFileScheme(String type) throws IOException {
		assertSupportedMountType(type);
		return providers.get(type);
	}
	
	@Override
	public VirtualFolder createOrUpdate(VirtualFolder folder, Collection<User> users, Collection<Role> roles) throws IOException {
		
		assertWrite(VirtualFolder.RESOURCE_KEY);

		folder.setMountPath(FileUtils.checkStartsWithSlash(folder.getMountPath()));
		
		FileScheme scheme = getFileScheme(folder.getType());
		scheme.configure(folder);
		
		try {
			AbstractFileFactory<?> m = resolveMount(folder);
			AbstractFile f = m.getFile("");
			if(!f.exists()) {
				throw new FileNotFoundException();
			}
			
			if(!f.isDirectory()) {
				throw new ObjectException("The target path is not a folder");
			}
			
		} catch(PermissionDeniedException | IOException e) {
			throw new ObjectException(String.format("Cannot resolve folder %s", folder.getName()), e);
		}
		
		folder.getRoles().clear();
		folder.getUsers().clear();
		
		for(Role role : roles) {
			folder.getRoles().add(role);
		}
		for(User user : users) {
			folder.getUsers().add(user);
		}
		
		repository.saveOrUpdate(folder);
		
		return folder;
	}
	
	@Override
	public VirtualFolder createOrUpdate(VirtualFolder folder) throws IOException {
		
		assertWrite(VirtualFolder.RESOURCE_KEY);
		
		folder.setMountPath(FileUtils.checkStartsWithSlash(folder.getMountPath()));
		
		FileScheme scheme = getFileScheme(folder.getType());
		scheme.configure(folder);
		
		try {
			AbstractFileFactory<?> m = resolveMount(folder);
			AbstractFile f = m.getFile("");
			if(!f.exists()) {
				throw new FileNotFoundException();
			}
			
			if(!f.isDirectory()) {
				throw new ObjectException("The target path is not a folder");
			}
			
		} catch(PermissionDeniedException | IOException e) {
			throw new ObjectException(String.format("Cannot resolve folder %s", folder.getName()), e);
		} 
		
		repository.saveOrUpdate(folder);
		
		return folder;
	}
	
	@Override
	public AbstractFileFactory<?> resolveMount(VirtualFolder folder) throws IOException {
		
		FileScheme scheme = getFileScheme(folder.getType());			
		return scheme.configureFactory(folder);
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
					if(scheme instanceof VFSFileScheme<?>) {
						if(!vfsMgr.hasProvider(scheme.getScheme())) {
							log.info("Registering {} file scheme", scheme.getScheme());
							vfsMgr.addProvider(scheme.getScheme(), ((VFSFileScheme<?>)scheme).getFileProvider());
						}
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
				log.info("Registering file scheme " + scheme.getName());
				schemes.add(scheme);
				providers.put(scheme.getResourceKey(), scheme);
				for(String type : scheme.types()) {
					providers.put(type, scheme);
				}
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
		
		
		FileScheme scheme = getFileScheme(folder.getType());

		String uri = replaceVariables(folder.getPath().generatePath());
		
		if(isEncrypting(folder)) {
			return new VirtualFolderMount(folder,
					uri,
					new EncryptingFileFactory(
							configureFactory(scheme, folder), 
							((PGPEncryptionExtension)folder).getPGPEncryption()), 
					scheme.createRoot());
		} else {
			return new VirtualFolderMount(folder,
					uri,
					configureFactory(scheme, folder), 
					scheme.createRoot());				
		}			
	}

	private AbstractFileFactory<?> configureFactory(FileScheme scheme, VirtualFolder folder) throws IOException {
		
		for(FileSchemeAdapter adapter : applicationService.getBeans(FileSchemeAdapter.class)) {
			if(adapter.isExtending(scheme)) {
				try {
					return adapter.configureFactory(scheme, folder);
				} catch (URISyntaxException e) {
					throw new IOException(e.getMessage(), e);
				}
			}
		}
		return scheme.configureFactory(folder);
	}

	private boolean isEncrypting(VirtualFolder folder) {
		return folder instanceof PGPEncryptionExtension && ((PGPEncryptionExtension)folder).getPGPEncryption().getEncrypt();
	}

	public String replaceVariables(String destinationUri) {
		return destinationUri.replace("%USERNAME%", getCurrentUser().getUsername())
				.replace("%INSTALLPATH%", new File(".").getAbsolutePath());
	}

	@Override
	public VirtualFolder getVirtualFolder(String mount) {
		return repository.getObject(VirtualFolder.class, SearchField.eq("mountPath", mount));
	}

	@Override
	public void deleteVirtualFolder(VirtualFolder virtualFolder) {
		assertWrite(VirtualFolder.RESOURCE_KEY);
		
		try {
			FileScheme scheme = getFileScheme(virtualFolder.getType());
			scheme.delete(virtualFolder);
			repository.deleteObject(virtualFolder);
			
		} catch(IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public Collection<FileScheme> getSchemes() {
		checkSchemes();
		List<FileScheme> tmp = new ArrayList<>(schemes);
		Collections.sort(tmp, new Comparator<FileScheme>() {

			@Override
			public int compare(FileScheme o1, FileScheme o2) {
				return o1.getWeight().compareTo(o2.getWeight());
			}
			
		});
		return Collections.<FileScheme>unmodifiableCollection(tmp);
	}

	@Override
	public VirtualFolder getHomeMount(User user) {
		VirtualFolder home;
		try {
			home = repository.getAssignedObject(VirtualFolder.class, user, SearchField.eq("path.appendUsername", Boolean.TRUE));
		} catch(ObjectException e) {
			home = repository.getAssignedObject(VirtualFolder.class, user, SearchField.eq("mountPath", "/"));
		}
		return home;
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
		try {
			createOrUpdate(folder);
			return folder.getUuid();
		} catch (IOException e) {
			throw new ObjectException(e);
		}
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
	public boolean isEnabled() {
		return true;
	}

	@Override
	public AbstractFile getFile(String virtualPath) throws PermissionDeniedException, IOException {
		return getFactory().getFile(virtualPath);
	}

	@Override
	public AbstractFileFactory<?> getFactory(boolean reset) {
		return getFactory(getCurrentUser(), reset);
	}

	@Override
	public AbstractFileFactory<?> getFactory(User user) {
		return getFactory(user, false);
	}
	
	@Override
	public AbstractFileFactory<?> getFactory(User user, boolean reset) {

		@SuppressWarnings("rawtypes")
		Map<String,AbstractFileFactory> cache = 
				cacheService.getCacheOrCreate(ABSTRACT_FILE_FACTORY, 
						String.class, AbstractFileFactory.class);
		
		AbstractFileFactory<?> factory = (AbstractFileFactory<?>) cache.get(user.getUuid());
		if(Objects.isNull(factory) || reset) {
			factory = sshdService.getFileFactory(user);	
		}
		
		cache.put(user.getUuid(), factory);
		return factory;
		
	}
	
	@Override
	public void resetFactory() {
		resetFactory(getCurrentUser());
	}
	
	@Override
	public void resetFactory(User user) {
		@SuppressWarnings("rawtypes")
		Map<String,AbstractFileFactory> cache = 
				cacheService.getCacheOrCreate(ABSTRACT_FILE_FACTORY, 
						String.class, AbstractFileFactory.class);
		cache.remove(getCurrentUser().getUuid());
	}

	@Override
	public VirtualFolder getParentMount(AbstractFile fileObject) {
		VirtualFile vf = (VirtualFile) fileObject;
		return getVirtualFolder(vf.getMount().getMount());
	}

	@Override
	public void onApplicationStartup() {
		
		eventService.changed(VirtualFolder.class, (e)->{
			@SuppressWarnings("rawtypes")
			Map<String,AbstractFileFactory> cache = 
					cacheService.getCacheOrCreate(ABSTRACT_FILE_FACTORY, 
							String.class, AbstractFileFactory.class);
			cache.clear();
		});
	}

	@Override
	protected Class<VirtualFolder> getResourceClass() {
		return VirtualFolder.class;
	}
}
