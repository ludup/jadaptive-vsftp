package com.jadaptive.plugins.ssh.vsftp.stats;

import java.io.Serializable;

public class MonthlyThroughputData implements Serializable {

	private static final long serialVersionUID = 8369490783122477716L;
	
	String direction;
	Double scp;
	Double sftp;
	Double https;
	
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public Double getScp() {
		return scp;
	}
	public void setScp(Double scp) {
		this.scp = scp;
	}
	public Double getSftp() {
		return sftp;
	}
	public void setSftp(Double sftp) {
		this.sftp = sftp;
	}
	public Double getHttps() {
		return https;
	}
	public void setHttps(Double https) {
		this.https = https;
	}
	
	
}
