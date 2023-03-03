package com.jadaptive.plugins.ssh.vsftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.AbstractFileAdapter;
import com.sshtools.common.permissions.PermissionDeniedException;

public class AbstractFileStatsAdapter extends AbstractFileAdapter {

	public AbstractFileStatsAdapter(AbstractFile file) {
		super(file);
	}

	@Override
	public InputStream getInputStream() throws IOException, PermissionDeniedException {
		// TODO Auto-generated method stub
		return super.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException, PermissionDeniedException {
		// TODO Auto-generated method stub
		return super.getOutputStream();
	}

	@Override
	public OutputStream getOutputStream(boolean append) throws IOException, PermissionDeniedException {
		// TODO Auto-generated method stub
		return super.getOutputStream(append);
	}

	

	
}
