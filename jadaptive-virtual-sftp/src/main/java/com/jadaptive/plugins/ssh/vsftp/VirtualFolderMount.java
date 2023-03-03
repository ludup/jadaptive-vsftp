package com.jadaptive.plugins.ssh.vsftp;

import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.files.vfs.VirtualMountTemplate;

public class VirtualFolderMount extends VirtualMountTemplate {

	VirtualFolder folder;
	
	public VirtualFolderMount(VirtualFolder folder, String path, AbstractFileFactory<?> actualFileFactory, boolean createMountFolder) {
		super(folder.getMountPath(), path, actualFileFactory, createMountFolder);
		this.folder = folder;
	}

	public VirtualFolderMount(VirtualFolder folder,  String path, AbstractFileFactory<?> actualFileFactory,
			boolean createMountFolder, long lastModified) {
		super(folder.getMountPath(), path, actualFileFactory, createMountFolder, lastModified);
		this.folder = folder;

	}

	public VirtualFolder getVirtualFolder() {
		return folder;
	}

}
