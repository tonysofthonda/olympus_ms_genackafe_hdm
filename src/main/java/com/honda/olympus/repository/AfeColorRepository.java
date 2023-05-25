package com.honda.olympus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.honda.olympus.dao.AfeModelColorEntity;

@Repository
public interface AfeColorRepository extends JpaRepository<AfeModelColorEntity, Long> {

	// QUERY2
	@Query("SELECT o FROM AfeModelColorEntity o WHERE o.id = :id ")
	public List<AfeModelColorEntity> findAllById(@Param("id") Long id);
}
