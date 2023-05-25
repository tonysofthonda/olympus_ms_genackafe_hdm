package com.honda.olympus.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "afe_color", schema = "afedb")
public class AfeColorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "code")
	private String code;

	@Column(name = "interior_code")
	private String interiorCode;

	@Column(name = "interior_name")
	private String interiorName;

	@Column(name = "invoice_interior_code")
	private String invoiceInteriorCode;

	@Column(name = "exterior_code")
	private String exteriorCode;

	@Column(name = "exterior_description")
	private String exteriorDescriptio;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_timestamp")
	Date creationTimeStamp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_timestamp")
	Date updateTimeStamp;

	public AfeColorEntity() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getInteriorCode() {
		return interiorCode;
	}

	public void setInteriorCode(String interiorCode) {
		this.interiorCode = interiorCode;
	}

	public String getInteriorName() {
		return interiorName;
	}

	public void setInteriorName(String interiorName) {
		this.interiorName = interiorName;
	}

	public String getInvoiceInteriorCode() {
		return invoiceInteriorCode;
	}

	public void setInvoiceInteriorCode(String invoiceInteriorCode) {
		this.invoiceInteriorCode = invoiceInteriorCode;
	}

	public String getExteriorCode() {
		return exteriorCode;
	}

	public void setExteriorCode(String exteriorCode) {
		this.exteriorCode = exteriorCode;
	}

	public String getExteriorDescriptio() {
		return exteriorDescriptio;
	}

	public void setExteriorDescriptio(String exteriorDescriptio) {
		this.exteriorDescriptio = exteriorDescriptio;
	}

	public Date getCreationTimeStamp() {
		return creationTimeStamp;
	}

	public void setCreationTimeStamp(Date creationTimeStamp) {
		this.creationTimeStamp = creationTimeStamp;
	}

	public Date getUpdateTimeStamp() {
		return updateTimeStamp;
	}

	public void setUpdateTimeStamp(Date updateTimeStamp) {
		this.updateTimeStamp = updateTimeStamp;
	}

}
