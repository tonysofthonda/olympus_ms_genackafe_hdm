package com.honda.olympus.vo;

public class ResponseVO {

	private Integer status;
	private String message;

	private String file;
	
	public String getMessage() {
		return message;
	}
	
	

	public ResponseVO(Integer status, String message, String file) {
		super();
		this.status = status;
		this.message = message;
		this.file = file;
	}



	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
	

	

}
