package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.mongodb.internal.HexUtils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;

public class File {

	AbstractFile file;
	boolean isMount;
	VirtualFolder parent;
	
	public File(AbstractFile file, boolean isMount, VirtualFolder parent) {
		this.file = file;
		this.isMount = isMount;
		this.parent = parent;
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
			return file.isReadable();
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
		return isMount;
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
}
