package com.honda.olympus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honda.olympus.dao.AfeOrdersActionHistoryEntity;

public interface AfeOrdersActionHistoryRepository extends JpaRepository<AfeOrdersActionHistoryEntity, Long>{

	// QUERY8
		@Query("SELECT o FROM AfeOrdersActionHistoryEntity o WHERE o.fixedOrderId = :fixedOrderId order by o.fixedOrderId desc ")
		public List<AfeOrdersActionHistoryEntity> findAllByFixedOrder(@Param("fixedOrderId") Long fixedOrderId);
	
}
