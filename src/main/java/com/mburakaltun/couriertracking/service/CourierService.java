package com.mburakaltun.couriertracking.service;

import com.mburakaltun.couriertracking.common.properties.AppProperties;
import com.mburakaltun.couriertracking.model.entity.CourierEntity;
import com.mburakaltun.couriertracking.model.entity.CourierLocationEntity;
import com.mburakaltun.couriertracking.model.entity.CourierStoreEntranceLogEntity;
import com.mburakaltun.couriertracking.model.entity.StoreEntity;
import com.mburakaltun.couriertracking.model.request.RequestLogCourierLocation;
import com.mburakaltun.couriertracking.model.request.RequestQueryCourierTotalDistance;
import com.mburakaltun.couriertracking.model.request.RequestCreateCourier;
import com.mburakaltun.couriertracking.model.response.ResponseLogCourierLocation;
import com.mburakaltun.couriertracking.model.response.ResponseQueryCourierTotalDistance;
import com.mburakaltun.couriertracking.model.response.ResponseCreateCourier;
import com.mburakaltun.couriertracking.repository.CourierJpaRepository;
import com.mburakaltun.couriertracking.repository.CourierLocationJpaRepository;
import com.mburakaltun.couriertracking.repository.CourierStoreEntranceLogJpaRepository;
import com.mburakaltun.couriertracking.repository.StoreJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
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
    private final CourierStoreEntranceLogJpaRepository courierStoreEntranceLogJpaRepository;

    public ResponseCreateCourier createCourier(RequestCreateCourier requestCourierLocation) {
        CourierEntity courierEntity = new CourierEntity();
        courierEntity.setName(requestCourierLocation.getName());
        courierEntity.setTotalDistance(0.0);
        CourierEntity savedCourier = courierJpaRepository.save(courierEntity);

        return ResponseCreateCourier.builder()
                .id(savedCourier.getId())
                .build();
    }

    public ResponseLogCourierLocation logCourierLocation(RequestLogCourierLocation requestLogCourierLocation) {
        String threadName = Thread.currentThread().getName();
        log.info("logCourierLocation executing in thread: {}", threadName);

        validateCourier(requestLogCourierLocation.getCourierId());
        saveCourierLocation(requestLogCourierLocation);
        saveCourierStoreEntrance(requestLogCourierLocation);
        updateCourierDistance(requestLogCourierLocation);

        return ResponseLogCourierLocation.builder()
                .success(true)
                .build();
    }

    public ResponseQueryCourierTotalDistance queryTotalDistance(RequestQueryCourierTotalDistance requestQueryCourierTotalDistance) {
        validateCourier(requestQueryCourierTotalDistance.getCourierId());
        Optional<CourierEntity> courierEntityOptional = courierJpaRepository.findById(requestQueryCourierTotalDistance.getCourierId());
        Double totalDistance = courierEntityOptional.map(CourierEntity::getTotalDistance).orElse(null);

        return ResponseQueryCourierTotalDistance.builder()
                .totalDistanceInMeters(totalDistance)
                .build();
    }

    private void updateCourierDistance(RequestLogCourierLocation requestLogCourierLocation) {
        String threadName = Thread.currentThread().getName();
        log.info("updateCourierDistance executing in thread: {}", threadName);

        Long courierId = requestLogCourierLocation.getCourierId();
        Double latitude = requestLogCourierLocation.getLatitude();
        Double longitude = requestLogCourierLocation.getLongitude();

        Optional<CourierEntity> courierEntityOptional = courierJpaRepository.findById(courierId);
        if (courierEntityOptional.isPresent()) {
            CourierEntity courierEntity = courierEntityOptional.get();
            List<CourierLocationEntity> courierLocations = courierLocationJpaRepository.findByCourierIdOrderByRecordedAtDesc(courierId);

            if (!courierLocations.isEmpty()) {
                CourierLocationEntity lastLocation = courierLocations.get(0);
                double distance = calculateDistanceInMeters(lastLocation.getLatitude(), lastLocation.getLongitude(), latitude, longitude);
                log.info("Courier ID: {}, Distance from last location: {} meters", courierId, distance);
                courierEntity.setTotalDistance(courierEntity.getTotalDistance() + distance);
            }

            courierJpaRepository.save(courierEntity);
        }
    }

    private void validateCourier(Long courierId) {
        if (courierId == null || !courierJpaRepository.existsById(courierId)) {
            throw new IllegalArgumentException("Courier with ID " + courierId + " does not exist.");
        }
    }

    private double calculateDistanceInMeters(Double fromLatitude, Double fromLongitude, Double toLatitude, Double toLongitude) {
        double latitudeDistance = Math.toRadians(toLatitude - fromLatitude);
        double longitudeDistance = Math.toRadians(toLongitude - fromLongitude);
        double a = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2) +
                Math.cos(Math.toRadians(fromLatitude)) * Math.cos(Math.toRadians(toLatitude)) *
                Math.sin(longitudeDistance / 2) * Math.sin(longitudeDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return appProperties.getEarthRadiusInKilometers() * c * 1000;
    }

    private void saveCourierLocation(RequestLogCourierLocation requestLogCourierLocation) {
        CourierLocationEntity courierLocationEntity = new CourierLocationEntity();
        courierLocationEntity.setCourierId(requestLogCourierLocation.getCourierId());
        courierLocationEntity.setRecordedAt(requestLogCourierLocation.getRecordedAt());
        courierLocationEntity.setLatitude(requestLogCourierLocation.getLatitude());
        courierLocationEntity.setLongitude(requestLogCourierLocation.getLongitude());
        courierLocationJpaRepository.save(courierLocationEntity);
    }

    private void saveCourierStoreEntrance(RequestLogCourierLocation requestLogCourierLocation) {
        Long courierId = requestLogCourierLocation.getCourierId();
        Double courierLatitude = requestLogCourierLocation.getLatitude();
        Double courierLongitude = requestLogCourierLocation.getLongitude();

        List<StoreEntity> storeEntityList = storeJpaRepository.findAll();
        for (StoreEntity storeEntity : storeEntityList) {
            Long storeId = storeEntity.getId();
            String storeName = storeEntity.getName();
            Double storeLatitude = storeEntity.getLatitude();
            Double storeLongitude = storeEntity.getLongitude();

            if (isEnteredStoreArea(courierLatitude, courierLongitude, storeLatitude, storeLongitude, storeName) && !isCourierAtStoreAreaWithinTimeThreshold(courierId, storeId)) {
                CourierStoreEntranceLogEntity courierStoreEntranceLogEntity = new CourierStoreEntranceLogEntity();
                courierStoreEntranceLogEntity.setCourierId(requestLogCourierLocation.getCourierId());
                courierStoreEntranceLogEntity.setStoreId(storeEntity.getId());
                courierStoreEntranceLogEntity.setEnteredAt(LocalDateTime.now());
                courierStoreEntranceLogEntity.setLastRecordedAt(LocalDateTime.now());
                courierStoreEntranceLogJpaRepository.save(courierStoreEntranceLogEntity);
            }
        }
    }

    private boolean isEnteredStoreArea(Double courierLatitude, Double courierLongitude, Double storeLatitude, Double storeLongitude, String storeName) {
        double distance = calculateDistanceInMeters(courierLatitude, courierLongitude, storeLatitude, storeLongitude);
        log.info("Courier is {} meters away from store {}", distance, storeName);
        return distance <= appProperties.getDistanceThresholdInMeters();
    }

    private boolean isCourierAtStoreAreaWithinTimeThreshold(Long courierId, Long storeId) {
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
}
