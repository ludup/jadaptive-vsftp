package com.jadaptive.plugins.ssh.vsftp.schemes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.FileProvider;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderOptions;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.files.vfs.VFSFileFactory;

public abstract class VFSFileScheme<T extends FileProvider> extends AbstractFileScheme {

	T provider; 

	@Autowired
	private VirtualFileService fileService; 
	
	protected VFSFileScheme(String resourceKey, String name, T provider, String... types) {
		super(resourceKey, name, types);
		this.provider = provider;
	}
	
	public FileSystemOptions buildFileSystemOptions(VirtualFolder folder) throws IOException {
		return new FileSystemOptions();
	}

	public URI generateUri(String path, FileSystemOptions opts) throws URISyntaxException {
		return new URI(getScheme() + "://" + path);
	}

	public T getFileProvider() {
		return provider;
	}

	@Override
	public VirtualFolder createFolder() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCredentials(VirtualFolder folder, VirtualFolderCredentials credentials) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOptions(VirtualFolder folder, VirtualFolderOptions options) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getBundle() {
		return VirtualFolder.RESOURCE_KEY;
	}
	
	@Override
	public void delete(VirtualFolder folder) {
		
	}
	
	@Override
	public AbstractFileFactory<?> configureFactory(VirtualFolder folder) throws IOException {
		
		FileSystemOptions opts = buildFileSystemOptions(folder);
		FileSystemManager manager = fileService.getManager(folder.getUuid(), CacheStrategy.ON_RESOLVE);
		
		try {
			return new VFSFileFactory(manager, opts, generateUri(
					fileService.replaceVariables(folder.getPath().generatePath()),
					opts).toASCIIString());
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
	
}
