package com.mburakaltun.couriertracking.repository;

import com.mburakaltun.couriertracking.model.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreJpaRepository extends JpaRepository<StoreEntity, Long> {
}
