package com.jadaptive.plugins.ssh.vsftp.schemes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.FileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

public abstract class AbstractFileScheme implements FileScheme {

	protected Logger log = LoggerFactory.getLogger(AbstractFileScheme.class);
	
	String name;
	String[] types;
	FileProvider provider; 
	
	protected AbstractFileScheme(String name, FileProvider provider, String... types) {
		this.name = name;
		this.types = types;
		this.provider = provider;
	}
	
	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder folder) throws IOException {
		return new FileSystemOptions();
	}

	@Override
	public boolean requiresCredentials() {
		return false;
	}

	@Override
	public Set<String> types() {
		return new HashSet<>(Arrays.asList(types));
	}

	@Override
	public URI generateUri(String path) throws URISyntaxException {
		return new URI(getScheme() + "://" + path);
	}

	@Override
	public FileProvider getFileProvider() {
		return provider;
	}

	@Override
	public EntityTemplate getCredentialsTemplate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends VirtualFolderCredentials> getCredentialsClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getScheme() {
		return types[0];
	}

	@Override
	public String getName() {
		return name;
	}

}
