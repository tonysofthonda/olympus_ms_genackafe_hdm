package com.honda.olympus.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MessageEventVO {

	@NotNull(message ="Status is mandatory")
	private Long status;

	@NotBlank(message ="Msg is mandatory")
	private String msg;

	@NotNull(message="Details are mandatory")
	private List<Long> details;

	public MessageEventVO() {
		super();
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
