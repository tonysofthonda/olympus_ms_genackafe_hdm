package com.honda.olympus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.honda.olympus.dao.AfePlantEntity;

@Repository
public interface AfePlantRepository extends JpaRepository<AfePlantEntity, Long>{
	
}
