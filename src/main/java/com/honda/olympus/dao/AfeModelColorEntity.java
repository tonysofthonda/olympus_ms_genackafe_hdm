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
@Table(name = "afe_model_color", schema = "afedb")
public class AfeModelColorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "model_id")
	private Long model_id;

	@Column(name = "color_id")
	private Long colorId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_timestamp")
	Date creationTimeStamp;

	@Column(name = "color_auto_id")
	Long colorAutoId;

	public AfeModelColorEntity() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getModel_id() {
		return model_id;
	}

	public void setModel_id(Long model_id) {
		this.model_id = model_id;
	}

	public Long getColorId() {
		return colorId;
	}

	public void setColorId(Long colorId) {
		this.colorId = colorId;
	}

	public Date getCreationTimeStamp() {
		return creationTimeStamp;
	}

	public void setCreationTimeStamp(Date creationTimeStamp) {
		this.creationTimeStamp = creationTimeStamp;
	}

	public Long getColorAutoId() {
		return colorAutoId;
	}

	public void setColorAutoId(Long colorAutoId) {
		this.colorAutoId = colorAutoId;
	}

}
