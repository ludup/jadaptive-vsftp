package com.jadaptive.plugins.ssh.vsftp.ui;

public class MountReportData {

	long totalSize;
	
	long httpDownloads;
	long httpUploads;
	
	long scpDownload;
	long scpUpload;
	
	long sftpDownload;
	long sftpUpload;
	
	String name;
	String type;
	
	public long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	public long getHttpDownloads() {
		return httpDownloads;
	}
	public void setHttpDownloads(long httpDownloads) {
		this.httpDownloads = httpDownloads;
	}
	public long getHttpUploads() {
		return httpUploads;
	}
	public void setHttpUploads(long httpUploads) {
		this.httpUploads = httpUploads;
	}
	public long getScpDownload() {
		return scpDownload;
	}
	public void setScpDownload(long scpDownload) {
		this.scpDownload = scpDownload;
	}
	public long getScpUpload() {
		return scpUpload;
	}
	public void setScpUpload(long scpUpload) {
		this.scpUpload = scpUpload;
	}
	public long getSftpDownload() {
		return sftpDownload;
	}
	public void setSftpDownload(long sftpDownload) {
		this.sftpDownload = sftpDownload;
	}
	public long getSftpUpload() {
		return sftpUpload;
	}
	public void setSftpUpload(long sftpUpload) {
		this.sftpUpload = sftpUpload;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
