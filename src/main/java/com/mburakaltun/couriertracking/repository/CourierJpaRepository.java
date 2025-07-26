package com.mburakaltun.couriertracking.repository;

import com.mburakaltun.couriertracking.model.entity.CourierEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourierJpaRepository extends JpaRepository<CourierEntity, Long> {
}
