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
@Table(name = "afe_model", schema = "afedb")
public class AfeModelEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "code")
	private String code;

	@Column(name = "model_type_id")
	private Long modelTypeId;

	@Column(name = "plant_id")
	private Long plantId;

	@Column(name = "description")
	private String description;

	@Column(name = "model_year")
	private Long modelYear;

	@Column(name = "door_quantity")
	private Long doorQuantity;

	@Column(name = "engine_cilynder_quantity")
	private Long engineCilynderQuantity;

	@Column(name = "fuel_type_code")
	private String fuelTypeCode;

	@Column(name = "division_id")
	private Long divisionId;

	@Column(name = "engine_type_number")
	private String engineTypeNumber;

	@Column(name = "weight")
	private Long weight;

	@Column(name = "packing_weight")
	private Long packingWeight;

	@Column(name = "volumen_size")
	private Long volumenSize;

	@Column(name = "leng_size")
	private Long leng_size;

	@Column(name = "width_size")
	private Long width_size;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_timestamp")
	Date creationTimeStamp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_timestamp")
	Date updateTimeStamp;

	@Column(name = "afe_record_status_id")
	private Long afeRecordStatusId;

	@Column(name = "exclud")
	private boolean exclud;

	
	public AfeModelEntity() {
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

	public Long getModelTypeId() {
		return modelTypeId;
	}

	public void setModelTypeId(Long modelTypeId) {
		this.modelTypeId = modelTypeId;
	}

	public Long getPlantId() {
		return plantId;
	}

	public void setPlantId(Long plantId) {
		this.plantId = plantId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getModelYear() {
		return modelYear;
	}

	public void setModelYear(Long modelYear) {
		this.modelYear = modelYear;
	}

	public Long getDoorQuantity() {
		return doorQuantity;
	}

	public void setDoorQuantity(Long doorQuantity) {
		this.doorQuantity = doorQuantity;
	}

	public Long getEngineCilynderQuantity() {
		return engineCilynderQuantity;
	}

	public void setEngineCilynderQuantity(Long engineCilynderQuantity) {
		this.engineCilynderQuantity = engineCilynderQuantity;
	}

	public String getFuelTypeCode() {
		return fuelTypeCode;
	}

	public void setFuelTypeCode(String fuelTypeCode) {
		this.fuelTypeCode = fuelTypeCode;
	}

	public Long getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Long divisionId) {
		this.divisionId = divisionId;
	}

	public String getEngineTypeNumber() {
		return engineTypeNumber;
	}

	public void setEngineTypeNumber(String engineTypeNumber) {
		this.engineTypeNumber = engineTypeNumber;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}

	public Long getPackingWeight() {
		return packingWeight;
	}

	public void setPackingWeight(Long packingWeight) {
		this.packingWeight = packingWeight;
	}

	public Long getVolumenSize() {
		return volumenSize;
	}

	public void setVolumenSize(Long volumenSize) {
		this.volumenSize = volumenSize;
	}

	public Long getLeng_size() {
		return leng_size;
	}

	public void setLeng_size(Long leng_size) {
		this.leng_size = leng_size;
	}

	public Long getWidth_size() {
		return width_size;
	}

	public void setWidth_size(Long width_size) {
		this.width_size = width_size;
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

	public Long getAfeRecordStatusId() {
		return afeRecordStatusId;
	}

	public void setAfeRecordStatusId(Long afeRecordStatusId) {
		this.afeRecordStatusId = afeRecordStatusId;
	}

	public boolean isExclud() {
		return exclud;
	}

	public void setExclud(boolean exclud) {
		this.exclud = exclud;
	}

}
