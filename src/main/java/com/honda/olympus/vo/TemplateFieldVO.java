package com.honda.olympus.vo;

public class TemplateFieldVO {

	public String fieldName;
	public Integer total;
	public Integer init;
	public Integer end;

	public TemplateFieldVO(String fieldName, Integer total, Integer init, Integer end) {
		super();
		this.fieldName = fieldName;
		this.total = total;
		this.init = init;
		this.end = end;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getInit() {
		return init;
	}

	public void setInit(Integer init) {
		this.init = init;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "TemplateFieldVO [fieldName=" + fieldName + ", total=" + total + ", init=" + init + ", end=" + end + "]";
	}

}
