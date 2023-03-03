package com.jadaptive.plugins.ssh.vsftp.zip;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.jadaptive.utils.FileUtils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.IOUtils;

public class ZipMultipleFilesInputStream extends InputStream {

	PipedOutputStream out = new PipedOutputStream();
	PipedInputStream in = new PipedInputStream(out, 1024000);
	InputStream currentSource;
	ZipOutputStream transformOut;
	boolean sourceEOF = false;
	int currentIndex = -1;
	List<AbstractFile> currentListing;
	String folder;
	
	public ZipMultipleFilesInputStream(String folder, List<AbstractFile> files) throws IOException, PermissionDeniedException {
		this.folder = folder;
		transformOut = new ZipOutputStream(out);
		currentIndex = -1;
		currentListing = files;
		processNextFile();
	}

	void processNextFile() throws IOException, PermissionDeniedException {
		
		if(currentIndex > -1) {
			transformOut.closeEntry();
			currentSource = null;
		}
		
		currentIndex++;
		
		if(currentIndex < currentListing.size()) {
			AbstractFile currentFile = currentListing.get(currentIndex);
			String filename = currentFile.getName();
			currentSource = currentFile.getInputStream();
			transformOut.putNextEntry(new ZipEntry(FileUtils.checkEndsWithSlash(folder) + filename));
			return;	
		}

		sourceEOF = true;
		transformOut.close();
		
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		
		byte[] tmp = new byte[len];
		
		while(!sourceEOF) {
			int r = currentSource.read(tmp, 0, tmp.length);
			
			if(r > -1) {
				transformOut.write(tmp, 0, r);
			} else if(r==-1) {
				IOUtils.closeStream(currentSource);
				try {
					processNextFile();
				} catch (IOException | PermissionDeniedException e) {
					throw new IOException(e.getMessage(), e);
				}
			}
			
			if(in.available() >= len) {
				break;
			}
		}
		
		if(in.available() > 0) {
			return in.read(b, off, len);
		}
		
		return -1;
	}
	
	public void close() throws IOException {
		IOUtils.closeStream(out);
		IOUtils.closeStream(in);
		IOUtils.closeStream(currentSource);
		if(!sourceEOF) {
			throw new IOException("Unexpected end of transformation stream");
		}
	}

	@Override
	public int read() throws IOException {
		byte[] b = new byte[1];
		int r = read(b);
		if(r==1) {
			return b[0] & 0xFF;
		}
		return r;
	}

}
