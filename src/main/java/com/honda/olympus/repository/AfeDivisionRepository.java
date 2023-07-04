package com.honda.olympus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.honda.olympus.dao.AfeDivisionEntity;

@Repository
public interface AfeDivisionRepository extends JpaRepository<AfeDivisionEntity, Long>{

}
