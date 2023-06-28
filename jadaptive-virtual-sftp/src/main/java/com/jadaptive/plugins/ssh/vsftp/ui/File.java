package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import org.apache.tomcat.util.buf.HexUtils;

import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.extensions.SharingExtension;
import com.jadaptive.plugins.ssh.vsftp.pgp.PGPEncryptionExtension;
import com.sshtools.common.files.vfs.VirtualFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.ssh.components.DigestUtils;

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
			return "f" + HexUtils.toHexString(DigestUtils.md5(getPath().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	public String getName() {
		return file.getName();
	}
	
	public boolean getEncrypted() {
		
		if(mount instanceof PGPEncryptionExtension) {
			return ((PGPEncryptionExtension)mount).getPGPEncryption().getEncrypt();
		}
		return false;
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
		if(parent==null) {
			return false;
		}
		if(parent instanceof SharingExtension) {
			return ((SharingExtension)parent).getSharing().getShareFiles();
		}
		return false;
	}
	
	public boolean isShareFolders() {
		if(parent==null) {
			return false;
		}
		if(parent instanceof SharingExtension) {
			return ((SharingExtension)parent).getSharing().getShareFolders();
		}
		return false;
	}
	
	public boolean isReadOnly() {
		return parent == null ? true : parent.getPath().getReadOnly();
	}
	
	public String getMountUuid() {
		return Objects.nonNull(mount) ? mount.getUuid() : "";
	}
}
