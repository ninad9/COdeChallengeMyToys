package com.myToys.codingTasks;

import java.io.Serializable;

public class PatentDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private String patentApplicationNumber;
	private String expiryDate;
	public String getPatentApplicationNumber() {
		return patentApplicationNumber;
	}
	public void setPatentApplicationNumber(String patentApplicationNumber) {
		this.patentApplicationNumber = patentApplicationNumber;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	

}
