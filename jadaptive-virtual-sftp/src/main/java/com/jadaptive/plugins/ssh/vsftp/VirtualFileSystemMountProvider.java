package com.jadaptive.plugins.ssh.vsftp;

import java.util.Collection;
import java.util.Collections;

import org.pf4j.Extension;

import com.jadaptive.api.sshd.PluginFileSystemMount;
import com.sshtools.common.files.vfs.VirtualMountTemplate;

@Extension
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
