package com.jadaptive.plugins.ssh.vsftp;

import java.io.IOException;
import java.net.URISyntaxException;

import org.pf4j.ExtensionPoint;

import com.sshtools.common.files.AbstractFileFactory;

public interface FileSchemeAdapter extends ExtensionPoint {

	boolean isExtending(FileScheme scheme);

	AbstractFileFactory<?> configureFactory(FileScheme scheme, VirtualFolder folder) throws IOException, URISyntaxException;

}
