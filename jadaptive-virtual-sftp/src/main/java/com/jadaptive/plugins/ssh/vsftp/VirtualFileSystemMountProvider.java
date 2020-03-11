package com.jadaptive.plugins.ssh.vsftp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.plugins.sshd.PluginFileSystemMount;
import com.sshtools.common.files.vfs.VFSFileFactory;
import com.sshtools.common.files.vfs.VirtualMountTemplate;

@Extension
public class VirtualFileSystemMountProvider implements PluginFileSystemMount {

	static Logger log = LoggerFactory.getLogger(VirtualFileSystemMountProvider.class);
	
	@Autowired
	private VirtualFileService fileService;  
	
	@Override
	public Collection<? extends VirtualMountTemplate> getAdditionalMounts() {
		
		List<VirtualMountTemplate> templates = new ArrayList<>();
		
		for(VirtualFolder folder : fileService.getVirtualFolders()) {
			try {
				templates.add(fileService.getVirtualMountTemplate(folder));
			} catch (IOException e) {
				log.error("Failed to process mount", e);
			}
		}
		return templates;
		
	}

	@Override
	public boolean hasHome() {
		return true;
	}

	@Override
	public VirtualMountTemplate getHomeMount() throws IOException {
		return new VirtualMountTemplate("/", "tmp:///", new VFSFileFactory(), true);
	}

}
