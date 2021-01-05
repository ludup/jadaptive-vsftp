package com.jadaptive.plugins.ssh.vsftp;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.FileProvider;

import com.jadaptive.api.repository.UUIDObjectService;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.user.User;
import com.sshtools.common.files.vfs.VFSFileFactory;
import com.sshtools.common.files.vfs.VirtualMountTemplate;

public interface VirtualFileService extends UUIDObjectService<VirtualFolder> {

	boolean checkMountExists(String mount, User user);

	boolean checkSupportedMountType(String type);

	FileScheme getFileScheme(String type);

	VFSFileFactory resolveMount(VirtualFolder folder) throws IOException;

	VirtualFolder createOrUpdate(VirtualFolder folder, Collection<User> users, Collection<Role> roles);

	Iterable<VirtualFolder> getVirtualFolders();

	VirtualMountTemplate getVirtualMountTemplate(VirtualFolder folder) throws IOException;

	VirtualFolder getVirtualFolder(String mount);

	void deleteVirtualFolder(VirtualFolder virtualFolder);

	void addProvider(String scheme, FileProvider provider) throws FileSystemException;

	Collection<FileScheme> getSchemes();

	VirtualFolder getHomeMount();

	VirtualFolder createOrUpdate(VirtualFolder folder);

	VirtualFolder getVirtualFolderByShortCode(String shortCode);

	Iterable<VirtualFolder> getPersonalFolders();

}
