package com.honda.olympus.vo;

import java.util.List;

public class MessageVO {

	private Long status;

	private String msg;

	private List<Long> details;

	public MessageVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<Long> getDetails() {
		return details;
	}

	public void setDetails(List<Long> details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "MessageVO [status=" + status + ", msg=" + msg + ", details=" + details + "]";
	}

}
