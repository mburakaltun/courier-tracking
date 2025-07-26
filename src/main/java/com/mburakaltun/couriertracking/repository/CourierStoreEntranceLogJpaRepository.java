package com.mburakaltun.couriertracking.repository;

import com.mburakaltun.couriertracking.model.entity.CourierStoreEntranceLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourierStoreEntranceLogJpaRepository extends JpaRepository<CourierStoreEntranceLogEntity, Long> {
    Optional<CourierStoreEntranceLogEntity> findByCourierIdAndStoreIdOrderByEnteredAtDesc(Long courierId, Long storeId);
}
