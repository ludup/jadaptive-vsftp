package com.jadaptive.plugins.ssh.vsftp;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.vfs2.FileObject;

import com.jadaptive.api.role.Role;
import com.jadaptive.api.user.User;
import com.sshtools.common.files.vfs.VirtualMountTemplate;

public interface VirtualFileService {

	boolean checkMountExists(String mount, User user);

	boolean checkSupportedMountType(String type);

	FileScheme getFileScheme(String type);

	FileObject resolveMount(VirtualFolder folder) throws IOException;

	VirtualFolder createOrUpdate(VirtualFolder folder, Collection<User> users, Collection<Role> roles);

	Collection<VirtualFolder> getVirtualFolders();

	VirtualMountTemplate getVirtualMountTemplate(VirtualFolder folder) throws IOException;

	VirtualFolder getVirtualFolder(String mount);

	void deleteVirtualFolder(VirtualFolder virtualFolder);

}
