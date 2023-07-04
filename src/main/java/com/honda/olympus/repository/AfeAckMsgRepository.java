package com.honda.olympus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.honda.olympus.dao.AfeAckMsgEntity;

@Repository
public interface AfeAckMsgRepository extends JpaRepository<AfeAckMsgEntity, Long>{
	
	// QUERY9
		@Query("SELECT o FROM AfeAckMsgEntity o WHERE o.afeOrderActionHistoryId = :afeOrderActionHistoryId ")
		public List<AfeAckMsgEntity> findAllByFixedOrderId(@Param("afeOrderActionHistoryId") Long afeOrderActionHistoryId);

}
