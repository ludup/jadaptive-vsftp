package com.jadaptive.plugins.ssh.vsftp.ui;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class BootstrapTableResult<T> {

	private boolean success;
	private String error;
	private long total;
	private Collection<?> rows;
	private T resource;
	
	public BootstrapTableResult(String error) {
		this.error = error;
		this.success = false;
	}
	
	public BootstrapTableResult(Collection<?> rows, long total) {
		this.rows = rows;
		this.total = total;
		this.success = true;
	}

	public boolean getSuccess() {
		return success;
	}
	
	public String getError() {
		return error;
	}
	
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public Collection<?> getRows() {
		return rows;
	}

	public void setRows(Collection<?> rows) {
		this.rows = rows;
	}

	public T getResource() {
		return resource;
	}

	public void setResource(T resource) {
		this.resource = resource;
	}


}