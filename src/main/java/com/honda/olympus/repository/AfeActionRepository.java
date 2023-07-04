package com.honda.olympus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.honda.olympus.dao.AfeActionEvEntity;

@Repository
public interface AfeActionRepository extends JpaRepository<AfeActionEvEntity, Long>{

	// QUERY5
	@Query("SELECT o FROM AfeActionEvEntity o WHERE o.id = :id ")
	public List<AfeActionEvEntity> findAllByAction(@Param("id") Long id);
	
}
