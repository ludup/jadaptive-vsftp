package com.jadaptive.plugins.ssh.vsftp;

import java.util.Collection;
import java.util.Collections;

import org.springframework.stereotype.Component;

import com.jadaptive.plugins.sshd.PluginFileSystemMount;
import com.sshtools.common.files.vfs.VirtualMountTemplate;

@Component
public class VirtualFileSystemMountProvider implements PluginFileSystemMount {

	@Override
	public Collection<? extends VirtualMountTemplate> getAdditionalMounts() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean hasHome() {
		return false;
	}

	@Override
	public VirtualMountTemplate getHomeMount() {
		return null;
	}

}
