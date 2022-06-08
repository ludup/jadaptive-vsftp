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
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.encrypt.EncryptionService;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderOptions;

public abstract class AbstractFileScheme<T extends FileProvider> implements FileScheme<T> {

	protected Logger log = LoggerFactory.getLogger(AbstractFileScheme.class);
	
	String name;
	String[] types;
	T provider; 
	String resourceKey; 
	
	@Autowired
	private EncryptionService encryptionService; 
	
	protected AbstractFileScheme(String resourceKey, String name, T provider, String... types) {
		this.resourceKey = resourceKey;
		this.name = name;
		this.types = types;
		this.provider = provider;
	}
	
	public String getResourceKey() {
		return resourceKey;
	}
	
	protected String decryptCredentials(String value) {
		return encryptionService.decrypt(value);
	}
	
	@Override
	public boolean createRoot() {
		return false;
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
	public boolean hasExtendedOptions() {
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
	public T getFileProvider() {
		return provider;
	}

	@Override
	public ObjectTemplate getOptionsTemplate() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void configure(VirtualFolder folder) {
		
	}

	@Override
	public Class<? extends VirtualFolderOptions> getOptionsClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ObjectTemplate getCredentialsTemplate() {
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
	
}
