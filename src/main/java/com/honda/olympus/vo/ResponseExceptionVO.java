package com.honda.olympus.vo;

public class ResponseExceptionVO {
	private String source;
	private Long status;
	private String message;
	private String file;

	public ResponseExceptionVO() {
	}
	
	

	public ResponseExceptionVO(String source, Long status, String message, String file) {
		super();
		this.source = source;
		this.status = status;
		this.message = message;
		this.file = file;
	}



	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	

}
