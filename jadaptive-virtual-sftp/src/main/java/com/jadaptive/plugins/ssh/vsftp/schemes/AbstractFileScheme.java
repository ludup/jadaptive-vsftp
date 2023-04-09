package com.jadaptive.plugins.ssh.vsftp.schemes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.encrypt.EncryptionService;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderOptions;

public abstract class AbstractFileScheme implements FileScheme {

	//protected Logger log = LoggerFactory.getLogger(AbstractFileScheme.class);
	
	String name;
	String[] types;
	String resourceKey; 
	
	@Autowired
	private EncryptionService encryptionService; 
	 
	
	protected AbstractFileScheme(String resourceKey, String name, String... types) {
		this.resourceKey = resourceKey;
		this.name = name;
		this.types = types;
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
	
	@Override
	public void delete(VirtualFolder folder) {
		
	}
}
