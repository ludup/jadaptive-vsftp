package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;

import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;

public class Mount {

	String text;
	String path;
	String icon;
	
	public Mount() {
	}
	
	public Mount(VirtualFolder folder, String icon) {
		setText(folder.getName());
		setPath(folder.getMountPath());
		setIcon(icon);
	}
	
	public Mount(AbstractFile file) {
		try {
			setText(file.getName());
			setPath(file.getAbsolutePath());
		} catch (IOException | PermissionDeniedException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public Mount(String text, String path, String icon) {
		setText(text);
		setPath(path);
		setIcon(icon);
	}

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
