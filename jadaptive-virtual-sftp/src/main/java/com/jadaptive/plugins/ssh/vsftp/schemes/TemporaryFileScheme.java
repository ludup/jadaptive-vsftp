package com.jadaptive.plugins.ssh.vsftp.schemes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.FileProvider;
import org.apache.commons.vfs2.provider.temp.TemporaryFileProvider;
import org.pf4j.Extension;

import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;

@Extension
public class TemporaryFileScheme implements FileScheme {

	FileProvider provider = new TemporaryFileProvider();
	
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
		return new HashSet<>(Arrays.asList("tmp"));
	}

	@Override
	public URI generateUri(String path) throws URISyntaxException {
		return new URI("tmp://" + path);
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
		return "tmp";
	}
}
