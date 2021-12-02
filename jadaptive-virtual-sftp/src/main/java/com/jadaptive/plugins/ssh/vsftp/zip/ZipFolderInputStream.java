package com.jadaptive.plugins.ssh.vsftp.zip;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.jadaptive.utils.FileUtils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.IOUtils;

public class ZipFolderInputStream extends InputStream {

	PipedOutputStream out = new PipedOutputStream();
	PipedInputStream in = new PipedInputStream(out, 1024000);
	InputStream currentSource;
	ZipOutputStream transformOut;
	boolean sourceEOF = false;
	AbstractFile rootFolder;
	AbstractFile currentFolder;
	int currentIndex = -1;
	List<AbstractFile> currentListing;
	List<AbstractFile> foldersToProcess = new ArrayList<>();
	List<String> filterFilenames;
	
	public ZipFolderInputStream(AbstractFile rootFolder) throws IOException, PermissionDeniedException {
		this.rootFolder = rootFolder;
		transformOut = new ZipOutputStream(out);
		currentFolder = rootFolder;
		currentIndex = -1;
		currentListing = rootFolder.getChildren();
		processNextFile();
	}
	
	public ZipFolderInputStream(AbstractFile rootFolder, List<String> filenames) throws IOException, PermissionDeniedException {
		this.rootFolder = rootFolder;
		transformOut = new ZipOutputStream(out);
		currentFolder = rootFolder;
		currentIndex = -1;
		currentListing = rootFolder.getChildren();
		this.filterFilenames = filenames;
		processNextFile();
	}
	
	void processNextFile() throws IOException, PermissionDeniedException {
		
		if(currentIndex > -1) {
			transformOut.closeEntry();
			currentSource = null;
		}
		currentIndex++;
		
		for(int i=currentIndex;i<currentListing.size();i++) {
			AbstractFile currentFile = currentListing.get(i);
			currentIndex = i;
			String childPath = getChildPath(currentFile);
			if(currentFile.isDirectory() && isFiltered(childPath)) {
				foldersToProcess.add(currentFile);
				continue;
			} else if(currentFile.isFile() && isFiltered(childPath)) {
				currentSource = currentFile.getInputStream();
				transformOut.putNextEntry(new ZipEntry(FileUtils.checkEndsWithNoSlash(childPath)));
				return;
			}
		}
		
		if(!foldersToProcess.isEmpty()) {
			currentFolder = foldersToProcess.remove(0);
			currentIndex = -1;
			transformOut.putNextEntry(new ZipEntry(FileUtils.checkEndsWithSlash(getChildPath(currentFolder))));
			transformOut.closeEntry();
			currentListing = currentFolder.getChildren();
			processNextFile();
		} else {
			sourceEOF = true;
			transformOut.close();
		}
		
	}
	
	boolean isFiltered(String childPath) {
		if(filterFilenames==null) {
			return true;
		} else {
			for(String filename : filterFilenames) { 
				if(childPath.startsWith(filename)) {
					return true;
				}
			}
		}
		return false;
	}
	
	String getChildPath(AbstractFile currentFile) throws IOException, PermissionDeniedException {
		return FileUtils.checkEndsWithNoSlash(FileUtils.stripParentPath(rootFolder.getCanonicalPath(), currentFile.getCanonicalPath()));
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
