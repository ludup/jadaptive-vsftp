package com.jadaptive.plugins.ssh.vsftp.schemes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.FileProvider;
import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
import org.pf4j.Extension;

import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@Extension
public class LocalFileScheme implements FileScheme {

	private final FileProvider provider = new DefaultLocalFileProvider();
	
	@Override
	public FileSystemOptions buildFileSystemOptions(VirtualFolder resource) throws IOException {
		return new FileSystemOptions();
	}

	@Override
	public boolean requiresCredentials() {
		return false;
	}

	@Override
	public Set<String> types() {
		return new HashSet<String>(Arrays.asList("file", "local"));
	}

	@Override
	public URI generateUri(String path) throws URISyntaxException {
		return new URI("file://" + path);
	}

	@Override
	public FileProvider getFileProvider() {
		return provider;
	}

	@Override
	public EntityTemplate getCredentialsTemplate() {
		throw new UnsupportedOperationException();
	}

}
