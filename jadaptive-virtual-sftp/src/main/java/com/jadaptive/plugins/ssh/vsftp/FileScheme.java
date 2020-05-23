package com.jadaptive.plugins.ssh.vsftp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.FileProvider;
import org.pf4j.ExtensionPoint;

import com.jadaptive.api.template.ObjectTemplate;

public interface FileScheme extends ExtensionPoint {

	FileSystemOptions buildFileSystemOptions(VirtualFolder folder) throws IOException;
	
	boolean requiresCredentials();

	Set<String> types();

	URI generateUri(String path) throws URISyntaxException;

	FileProvider getFileProvider();
	
	ObjectTemplate getCredentialsTemplate();

	Class<? extends VirtualFolderCredentials> getCredentialsClass();

	ObjectTemplate getOptionsTemplate();
	
	Class<? extends VirtualFolderOptions> getOptionsClass();
	
	String getScheme();

	String getName();

	boolean createRoot();

	boolean hasExtendedOptions();
}
