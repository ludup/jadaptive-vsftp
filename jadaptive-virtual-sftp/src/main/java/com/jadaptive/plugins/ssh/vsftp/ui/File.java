package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.mongodb.internal.HexUtils;
import com.sshtools.common.files.vfs.VirtualFile;
import com.sshtools.common.permissions.PermissionDeniedException;

public class File {

	VirtualFile file;
	VirtualFolder parent;
	VirtualFolder mount;
	
	public File(VirtualFile file, VirtualFolder parent, VirtualFolder mount) {
		this.file = file;
		this.parent = parent;
		this.mount = mount;
	}
	
	public String getId() {
		try {
			return "f" + HexUtils.hexMD5(getPath().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	public String getName() {
		return file.getName();
	}
	
	public boolean getEncrypted() {
		return mount!=null ? mount.getEncrypt() : parent != null ? parent.getEncrypt() : false;
	}
	
	public long getLength() {
		try {
			return file.length();
		} catch (IOException | PermissionDeniedException e) {
			return 0L;
		}
	}
	
	public String getPath() {
		try {
			return file.getAbsolutePath();
		} catch (IOException | PermissionDeniedException e) {
			return "";
		}
	}
	
	public boolean isDirectory() {
		try {
			return file.isDirectory();
		} catch (IOException | PermissionDeniedException e) {
			return false;
		}
	}
	
	public boolean isHidden() {
		try {
			return file.isHidden();
		} catch (IOException | PermissionDeniedException e) {
			return false;
		}
	}
	
	public boolean isWritable() {
		try {
			return file.isWritable();
		} catch (IOException | PermissionDeniedException e) {
			return false;
		}
	}
	
	public boolean isReadable() {
		try {
			return !isReadOnly() && file.isReadable();
		} catch (IOException | PermissionDeniedException e) {
			return false;
		}
	}
	
	public long getLastModified() {
		try {
			return file.lastModified();
		} catch (IOException | PermissionDeniedException e) {
			return 0L;
		}
	}
	
	public boolean isPublic() {
		return parent == null ? false : parent.isPublicFolder();
	}
	
	public boolean isMount() {
		return file.isMount();
	}
	
	public boolean isShareFiles() {
		return parent == null ? false : parent.getShareFiles();
	}
	
	public boolean isShareFolders() {
		return parent == null ? false : parent.getShareFolders();
	}
	
	public boolean isReadOnly() {
		return parent == null ? true : parent.getReadOnly();
	}
	
	public String getMountUuid() {
		return Objects.nonNull(mount) ? mount.getUuid() : "";
	}
}
