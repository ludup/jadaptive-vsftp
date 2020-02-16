package com.jadaptive.plugins.ssh.vsftp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.db.AssignableObjectDatabase;
import com.jadaptive.api.entity.EntityNotFoundException;
import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.user.User;
import com.sshtools.common.files.AbstractFileHomeFactory;
import com.sshtools.common.files.vfs.VFSFileFactory;
import com.sshtools.common.files.vfs.VirtualMountTemplate;
import com.sshtools.common.ssh.SshConnection;

@Service
public class VirtualFileServiceImpl implements VirtualFileService {

	static Logger log = LoggerFactory.getLogger(VirtualFileServiceImpl.class);
	
	@Autowired
	private AssignableObjectDatabase<VirtualFolder> repository;
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	private ApplicationService applicationService; 

	
	private Set<String> types;
	private Map<String, FileScheme> providers = new HashMap<>();
	private Map<String, FileSystemManager> managers = new HashMap<>();
	
	@Override
	public Collection<VirtualFolder> getVirtualFolders() {
		return repository.getAssignedObjects(
				VirtualFolder.class, 
				permissionService.getCurrentUser());
	}
	
//	public void mountS3(String name, String region, String accessKeyId, String secretAccessKey) {
//		FileSystemOptions opts = new FileSystemOptions();
//        try {
//        	
//            DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, 
//            		new StaticUserAuthenticator(null, accessKeyId, secretAccessKey));
//            
//            S3FileSystemConfigBuilder.getInstance().setRegion(opts, 
//            		Regions.fromName(region));
//        } catch (FileSystemException e) {
//            log.error(String.format("Failed to set credentials on %s", name));
//        }
//	}

	@Override
	public boolean checkMountExists(String mount, User user) {
		
		try {
			repository.getObjectByUUID(VirtualFolder.class, mount);
			return true;
		} catch(EntityNotFoundException e) { } 
		return false;
	}

	@Override
	public boolean checkSupportedMountType(String type) {
		
		if(Objects.isNull(types)) {
			types = new HashSet<>();
			for(FileScheme provider : applicationService.getBeans(FileScheme.class)) {
				types.addAll(provider.types());
				for(String t : provider.types()) {
					log.info("Registering file scheme {}", t);
					providers.put(t, provider);
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
		
		permissionService.assertReadWrite(VirtualFolder.RESOURCE_KEY);
		
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
			
			return new VFSFileFactory(mgr, opts, new VFSHomeFactory());
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
	
	public FileSystemManager getManager(String id, CacheStrategy cacheStrategy)
			throws FileSystemException {
		synchronized (managers) {
			String key = id == null ? "__DEFAULT__" : id;
			FileSystemManager mgr = managers.get(key);
			if (mgr == null) {
				DefaultFileSystemManager vfsMgr = new DefaultFileSystemManager();
				vfsMgr.setLogger(LogFactory.getLog(key));
				vfsMgr.setCacheStrategy(cacheStrategy);
				for (Map.Entry<String, FileScheme> en : providers.entrySet()) {
					if(!vfsMgr.hasProvider(en.getKey()))
						vfsMgr.addProvider(en.getKey(), en.getValue().getFileProvider());
				}
				vfsMgr.init();
				managers.put(key, vfsMgr);
				mgr = vfsMgr;
			}
			return mgr;
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
					new VFSFileFactory(manager, opts, new VFSHomeFactory()));
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	
	}

	class VFSHomeFactory implements AbstractFileHomeFactory {

		@Override
		public String getHomeDirectory(SshConnection con) {
			return "/";
		}

	}

	@Override
	public VirtualFolder getVirtualFolder(String mount) {
		return repository.getObjectByUUID(VirtualFolder.class, mount);
	}

	@Override
	public void deleteVirtualFolder(VirtualFolder virtualFolder) {
		permissionService.assertReadWrite(VirtualFolder.RESOURCE_KEY);
		repository.deleteObject(virtualFolder);
		
	}
}
