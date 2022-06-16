package com.jadaptive.plugins.ssh.vsftp.stats;

import java.io.Serializable;

public class DateValue implements Serializable {

	private static final long serialVersionUID = -1259347101839294441L;
	
	long date;
	Double value;
	
	public DateValue(long date, Double value) {
		this.date = date;
		this.value = value;
	}
	
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	
	
}
