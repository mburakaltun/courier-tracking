package com.mburakaltun.couriertracking.service;

import com.mburakaltun.couriertracking.common.properties.AppProperties;
import com.mburakaltun.couriertracking.model.entity.CourierStoreEntranceLogEntity;
import com.mburakaltun.couriertracking.repository.CourierStoreEntranceLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourierStoreEntranceLogService {

    private final AppProperties appProperties;
    private final CourierStoreEntranceLogJpaRepository courierStoreEntranceLogJpaRepository;

    public boolean hasRecentStoreEntranceRecord(Long courierId, Long storeId) {
        List<CourierStoreEntranceLogEntity> entranceLogs = courierStoreEntranceLogJpaRepository.findByCourierIdAndStoreIdOrderByEnteredAtDesc(courierId, storeId);

        if (CollectionUtils.isEmpty(entranceLogs)) {
            return false;
        }

        CourierStoreEntranceLogEntity courierStoreEntranceLogEntity = entranceLogs.get(0);
        double secondsSinceLastEntrance = Duration.between(courierStoreEntranceLogEntity.getLastRecordedAt(), LocalDateTime.now()).getSeconds();
        log.info("Courier ID: {}, Store ID: {}, Seconds since last record: {}", courierId, storeId, secondsSinceLastEntrance);

        if (secondsSinceLastEntrance > appProperties.getTimeThresholdInSeconds()) {
            return false;
        }

        courierStoreEntranceLogEntity.setLastRecordedAt(LocalDateTime.now());
        courierStoreEntranceLogJpaRepository.save(courierStoreEntranceLogEntity);
        return true;
    }

    public void createStoreEntranceLog(Long courierId, Long storeId) {
        CourierStoreEntranceLogEntity courierStoreEntranceLogEntity = new CourierStoreEntranceLogEntity();
        courierStoreEntranceLogEntity.setCourierId(courierId);
        courierStoreEntranceLogEntity.setStoreId(storeId);
        courierStoreEntranceLogEntity.setEnteredAt(LocalDateTime.now());
        courierStoreEntranceLogEntity.setLastRecordedAt(LocalDateTime.now());
        courierStoreEntranceLogJpaRepository.save(courierStoreEntranceLogEntity);
    }
}
