package com.mburakaltun.couriertracking.service;

import com.mburakaltun.couriertracking.common.properties.AppProperties;
import com.mburakaltun.couriertracking.model.entity.CourierEntity;
import com.mburakaltun.couriertracking.model.entity.CourierLocationEntity;
import com.mburakaltun.couriertracking.model.entity.StoreEntity;
import com.mburakaltun.couriertracking.model.request.RequestLogCourierLocation;
import com.mburakaltun.couriertracking.model.request.RequestQueryCourierTotalDistance;
import com.mburakaltun.couriertracking.model.response.ResponseLogCourierLocation;
import com.mburakaltun.couriertracking.model.response.ResponseQueryCourierTotalDistance;
import com.mburakaltun.couriertracking.repository.CourierJpaRepository;
import com.mburakaltun.couriertracking.repository.CourierLocationJpaRepository;
import com.mburakaltun.couriertracking.repository.StoreJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourierService {

    private final AppProperties appProperties;
    private final CourierJpaRepository courierJpaRepository;
    private final StoreJpaRepository storeJpaRepository;
    private final CourierLocationJpaRepository courierLocationJpaRepository;
    private final DistanceCalculator distanceCalculator;
    private final CourierStoreEntranceLogService courierStoreEntranceLogService;

    public ResponseLogCourierLocation logCourierLocation(RequestLogCourierLocation requestLogCourierLocation) {
        validateCourierExists(requestLogCourierLocation.getCourierId());
        persistCourierLocation(requestLogCourierLocation);
        checkAndLogStoreEntrances(requestLogCourierLocation);

        return ResponseLogCourierLocation.builder()
                .success(true)
                .build();
    }

    public ResponseQueryCourierTotalDistance queryTotalDistance(RequestQueryCourierTotalDistance requestQueryCourierTotalDistance) {
        validateCourierExists(requestQueryCourierTotalDistance.getCourierId());
        Optional<CourierEntity> courierEntityOptional = courierJpaRepository.findById(requestQueryCourierTotalDistance.getCourierId());
        Double totalDistance = courierEntityOptional.map(CourierEntity::getTotalDistance).orElse(null);

        return ResponseQueryCourierTotalDistance.builder()
                .totalDistanceInMeters(totalDistance)
                .build();
    }

    private void validateCourierExists(Long courierId) {
        if (courierId == null || !courierJpaRepository.existsById(courierId)) {
            throw new IllegalArgumentException("Courier with ID " + courierId + " does not exist.");
        }
    }

    private void persistCourierLocation(RequestLogCourierLocation requestLogCourierLocation) {
        CourierLocationEntity courierLocationEntity = new CourierLocationEntity();
        courierLocationEntity.setCourierId(requestLogCourierLocation.getCourierId());
        courierLocationEntity.setRecordedAt(requestLogCourierLocation.getRecordedAt());
        courierLocationEntity.setLatitude(requestLogCourierLocation.getLatitude());
        courierLocationEntity.setLongitude(requestLogCourierLocation.getLongitude());
        courierLocationJpaRepository.save(courierLocationEntity);
    }

    private void checkAndLogStoreEntrances(RequestLogCourierLocation requestLogCourierLocation) {
        Long courierId = requestLogCourierLocation.getCourierId();
        Double courierLatitude = requestLogCourierLocation.getLatitude();
        Double courierLongitude = requestLogCourierLocation.getLongitude();

        List<StoreEntity> storeEntityList = storeJpaRepository.findAll();
        for (StoreEntity storeEntity : storeEntityList) {
            Long storeId = storeEntity.getId();
            String storeName = storeEntity.getName();
            Double storeLatitude = storeEntity.getLatitude();
            Double storeLongitude = storeEntity.getLongitude();

            if (isCourierWithinStoreRadius(courierLatitude, courierLongitude, storeLatitude, storeLongitude, storeName) && !courierStoreEntranceLogService.hasRecentStoreEntranceRecord(courierId, storeId)) {
                courierStoreEntranceLogService.createStoreEntranceLog(requestLogCourierLocation.getCourierId(), storeEntity.getId());
            }
        }
    }

    private boolean isCourierWithinStoreRadius(Double courierLatitude, Double courierLongitude, Double storeLatitude, Double storeLongitude, String storeName) {
        double distance = distanceCalculator.calculateDistanceInMeters(courierLatitude, courierLongitude, storeLatitude, storeLongitude);
        log.info("Courier is {} meters away from store {}", distance, storeName);
        return distance <= appProperties.getDistanceThresholdInMeters();
    }
}