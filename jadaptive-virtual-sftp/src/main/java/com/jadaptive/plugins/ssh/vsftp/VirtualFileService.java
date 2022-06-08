package com.jadaptive.plugins.ssh.vsftp;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.FileProvider;

import com.jadaptive.api.repository.UUIDObjectService;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.stats.ResourceService;
import com.jadaptive.api.user.User;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.files.vfs.VFSFileFactory;
import com.sshtools.common.files.vfs.VirtualMountTemplate;
import com.sshtools.common.permissions.PermissionDeniedException;

public interface VirtualFileService extends UUIDObjectService<VirtualFolder>, ResourceService {

	boolean checkMountExists(String mount, User user);

	boolean checkSupportedMountType(String type);

	FileScheme<?> getFileScheme(String type);

	VFSFileFactory resolveMount(VirtualFolder folder) throws IOException;

	VirtualFolder createOrUpdate(VirtualFolder folder, Collection<User> users, Collection<Role> roles);

	Iterable<VirtualFolder> allObjects();

	VirtualMountTemplate getVirtualMountTemplate(VirtualFolder folder) throws IOException;

	VirtualFolder getVirtualFolder(String mount);

	void deleteVirtualFolder(VirtualFolder virtualFolder);

	void addProvider(String scheme, FileProvider provider) throws FileSystemException;

	Collection<FileScheme<?>> getSchemes();

	VirtualFolder getHomeMount(User user);

	VirtualFolder createOrUpdate(VirtualFolder folder);

	VirtualFolder getVirtualFolderByShortCode(String shortCode);

	Iterable<VirtualFolder> getPersonalFolders();

	AbstractFile getFile(String virtualPath) throws PermissionDeniedException, IOException;

	AbstractFileFactory<?> getFactory(boolean reset);

	AbstractFileFactory<?> getFactory(User user, boolean reset);
	
	void resetFactory();

	default AbstractFileFactory<?> getFactory() { return getFactory(false); }

	void resetFactory(User user);

	AbstractFileFactory<?> getFactory(User user);

}
