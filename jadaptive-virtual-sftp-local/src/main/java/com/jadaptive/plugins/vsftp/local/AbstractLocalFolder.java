package com.jadaptive.plugins.vsftp.local;

import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolderPath;

public abstract class AbstractLocalFolder extends VirtualFolder {

	private static final long serialVersionUID = 1L;
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	LocalFolderPath path;
	
	public VirtualFolderPath getPath() {
		return path;
	}
	
	public void setPath(VirtualFolderPath path) {
		this.path = (LocalFolderPath) path;
	}
}
