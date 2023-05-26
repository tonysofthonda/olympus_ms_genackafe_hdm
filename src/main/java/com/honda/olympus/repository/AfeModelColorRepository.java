package com.honda.olympus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honda.olympus.dao.AfeModelColorEntity;

public interface AfeModelColorRepository extends JpaRepository<AfeModelColorEntity, Long>{
	
	// QUERY3
		@Query("SELECT o FROM AfeModelColorEntity o WHERE o.id = :id ")
		List<AfeModelColorEntity> findAllById(@Param("id") Long id);

}
