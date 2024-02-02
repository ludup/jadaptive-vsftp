package com.jadaptive.plugins.ssh.vsftp.sendto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.IOUtils;

public class ZipTransfer {

	OutputStream out;
	ZipOutputStream transformOut;
	
	public ZipTransfer(OutputStream out) throws IOException, PermissionDeniedException {
		transformOut = new ZipOutputStream(out);
	}
	
	public void sendFile(String filename, InputStream in) throws IOException {
		transformOut.putNextEntry(new ZipEntry(filename));
		IOUtils.copy(in, transformOut);
		transformOut.closeEntry();
	}


	public void close() throws IOException {
		transformOut.close();
		IOUtils.closeStream(out);
	}

}
