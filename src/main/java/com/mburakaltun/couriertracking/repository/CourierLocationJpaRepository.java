package com.mburakaltun.couriertracking.repository;

import com.mburakaltun.couriertracking.model.entity.CourierLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourierLocationJpaRepository extends JpaRepository<CourierLocationEntity, Long> {
    List<CourierLocationEntity> findByCourierIdOrderByRecordedAtDesc(Long courierId);
}
