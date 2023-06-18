package com.jadaptive.plugins.ssh.vsftp;

import java.io.IOException;
import java.util.Set;

import org.pf4j.ExtensionPoint;

import com.jadaptive.api.template.ObjectTemplate;
import com.sshtools.common.files.AbstractFileFactory;

public interface FileScheme extends ExtensionPoint {

	String getResourceKey();
	
	boolean requiresCredentials();

	Set<String> types();

	ObjectTemplate getPathTemplate();
	
	Class<? extends VirtualFolderPath> getPathClass();
	
	ObjectTemplate getCredentialsTemplate();

	Class<? extends VirtualFolderCredentials> getCredentialsClass();

	ObjectTemplate getOptionsTemplate();
	
	Class<? extends VirtualFolderOptions> getOptionsClass();
	
	String getScheme();

	String getName();

	boolean createRoot();

	boolean hasExtendedOptions();

	VirtualFolder createFolder();

	void setCredentials(VirtualFolder folder, VirtualFolderCredentials credentials);

	void setOptions(VirtualFolder folder, VirtualFolderOptions generateMountOptions);

	String getIcon();

	VirtualFolder createVirtualFolder(String name, String mountPath, VirtualFolderPath path, VirtualFolderCredentials creds);

	String getBundle();

	Integer getWeight();

	void configure(VirtualFolder folder);
	
	default boolean isEnabled() { return true; }

	void delete(VirtualFolder virtualFolder);

	AbstractFileFactory<?> configureFactory(VirtualFolder folder) throws IOException;

	default void init() { };

}
