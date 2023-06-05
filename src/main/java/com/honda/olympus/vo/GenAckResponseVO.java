package com.honda.olympus.vo;

public class GenAckResponseVO {
	
	private Boolean success;
	private String fileName;
	
	
	
	public GenAckResponseVO(Boolean success, String fileName) {
		super();
		this.success = success;
		this.fileName = fileName;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	

}
