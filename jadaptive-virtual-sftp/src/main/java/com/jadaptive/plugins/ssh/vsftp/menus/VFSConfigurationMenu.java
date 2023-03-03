package com.jadaptive.plugins.ssh.vsftp.menus;

import java.util.Arrays;
import java.util.Collection;

import org.pf4j.Extension;

import com.jadaptive.api.config.ConfigurationPageItem;
import com.jadaptive.plugins.ssh.vsftp.VFSConfiguration;

@Extension
public class VFSConfigurationMenu implements ConfigurationPageItem {

	@Override
	public String getResourceKey() {
		return "vfsConfiguration";
	}

	@Override
	public String getBundle() {
		return VFSConfiguration.RESOURCE_KEY;
	}

	@Override
	public String getPath() {
		return "/app/ui/config/vfsConfiguration";
	}

	@Override
	public Collection<String> getPermissions() {
		return Arrays.asList("vfsConfiguration.read");
	}

	@Override
	public String getIcon() {
		return "fa-folder-open";
	}

	@Override
	public Integer weight() {
		return 1000;
	}

}
