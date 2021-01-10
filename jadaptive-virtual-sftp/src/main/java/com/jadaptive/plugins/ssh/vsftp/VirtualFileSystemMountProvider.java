package com.jadaptive.plugins.ssh.vsftp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.user.User;
import com.jadaptive.plugins.sshd.PluginFileSystemMount;
import com.sshtools.common.files.ReadOnlyFileFactoryAdapter;
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
			if(folder.getMountPath().equals("/")) {
				/**
				 * Skip the home mount
				 */
				continue;
			}
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
	public VirtualMountTemplate getHomeMount(User user) throws IOException {
		try {
			return fileService.getVirtualMountTemplate(fileService.getHomeMount());
		} catch(ObjectNotFoundException e) {
			return new VirtualMountTemplate("/", 
					String.format("ram://%s", user.getUsername()), 
						new ReadOnlyFileFactoryAdapter(new VFSFileFactory()), true);
		}
	}

}
