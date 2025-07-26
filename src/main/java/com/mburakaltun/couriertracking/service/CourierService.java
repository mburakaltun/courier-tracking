package com.mburakaltun.couriertracking.service;

import com.mburakaltun.couriertracking.model.entity.CourierLocationEntity;
import com.mburakaltun.couriertracking.model.entity.CourierStoreEntranceLogEntity;
import com.mburakaltun.couriertracking.model.entity.StoreEntity;
import com.mburakaltun.couriertracking.model.request.RequestCourierLocation;
import com.mburakaltun.couriertracking.model.request.RequestCourierTotalDistance;
import com.mburakaltun.couriertracking.model.response.ResponseCourierLocation;
import com.mburakaltun.couriertracking.model.response.ResponseCourierTotalDistance;
import com.mburakaltun.couriertracking.repository.CourierLocationJpaRepository;
import com.mburakaltun.couriertracking.repository.CourierStoreEntranceLogJpaRepository;
import com.mburakaltun.couriertracking.repository.StoreJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourierService {

    private final static int EARTH_RADIUS = 6371;
    private final static double DISTANCE_THRESHOLD = 0.1; // Distance threshold in kilometers

    private final StoreJpaRepository storeJpaRepository;
    private final CourierLocationJpaRepository courierLocationJpaRepository;
    private final CourierStoreEntranceLogJpaRepository courierStoreEntranceLogJpaRepository;

    public ResponseCourierLocation logCourierLocation(RequestCourierLocation requestCourierLocation) {
        saveCourierLocation(requestCourierLocation);
        saveCourierStoreEntrance(requestCourierLocation);

        return ResponseCourierLocation.builder()
                .build();
    }

    public ResponseCourierTotalDistance calculateTotalDistance(RequestCourierTotalDistance requestCourierTotalDistance) {
        List<CourierLocationEntity> courierLocationEntities = courierLocationJpaRepository.findByCourierIdOrderByRecordedAtDesc(requestCourierTotalDistance.getCourierId());

        if (courierLocationEntities.size() < 2) {
            return ResponseCourierTotalDistance.builder()
                    .totalDistance(0.0)
                    .build();
        }

        double totalDistance = 0.0;

        for (int i = 0; i < courierLocationEntities.size() - 1; i++) {
            CourierLocationEntity currentLocation = courierLocationEntities.get(i);
            CourierLocationEntity nextLocation = courierLocationEntities.get(i + 1);
            double distance = calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), nextLocation.getLatitude(), nextLocation.getLongitude());
            totalDistance += distance;
        }

        return ResponseCourierTotalDistance.builder()
                .totalDistance(totalDistance)
                .build();
    }

    private double calculateDistance(Double fromLatitude, Double fromLongitude, Double toLatitude, Double toLongitude) {
        double latitudeDistance = Math.toRadians(toLatitude - fromLatitude);
        double longitudeDistance = Math.toRadians(toLongitude - fromLongitude);
        double a = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2) +
                Math.cos(Math.toRadians(fromLatitude)) * Math.cos(Math.toRadians(toLatitude)) *
                Math.sin(longitudeDistance / 2) * Math.sin(longitudeDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    private void saveCourierLocation(RequestCourierLocation requestCourierLocation) {
        CourierLocationEntity courierLocationEntity = new CourierLocationEntity();
        courierLocationEntity.setCourierId(requestCourierLocation.getCourierId());
        courierLocationEntity.setRecordedAt(requestCourierLocation.getRecordedAt());
        courierLocationEntity.setLatitude(requestCourierLocation.getLatitude());
        courierLocationEntity.setLongitude(requestCourierLocation.getLongitude());
        courierLocationJpaRepository.save(courierLocationEntity);
    }

    private void saveCourierStoreEntrance(RequestCourierLocation requestCourierLocation) {
        List<StoreEntity> storeEntityList = storeJpaRepository.findAll();
        for (StoreEntity storeEntity : storeEntityList) {
            Double courierLatitude = requestCourierLocation.getLatitude();
            Double courierLongitude = requestCourierLocation.getLongitude();
            Double storeLatitude = storeEntity.getLatitude();
            Double storeLongitude = storeEntity.getLongitude();

            if (isEnteredStoreArea(courierLatitude, courierLongitude, storeLatitude, storeLongitude) && isCourierAtStoreInOneMinute(requestCourierLocation.getCourierId(), storeEntity.getId())) {
                CourierStoreEntranceLogEntity courierStoreEntranceLogEntity = new CourierStoreEntranceLogEntity();
                courierStoreEntranceLogEntity.setCourierId(requestCourierLocation.getCourierId());
                courierStoreEntranceLogEntity.setStoreId(storeEntity.getId());
                courierStoreEntranceLogEntity.setEnteredAt(LocalDateTime.now());
                courierStoreEntranceLogJpaRepository.save(courierStoreEntranceLogEntity);
            }
        }
    }

    private boolean isEnteredStoreArea(Double courierLatitude, Double courierLongitude, Double storeLatitude, Double storeLongitude) {
        double distance = calculateDistance(courierLatitude, courierLongitude, storeLatitude, storeLongitude);
        return distance <= DISTANCE_THRESHOLD;
    }

    private boolean isCourierAtStoreInOneMinute(Long courierId, Long storeId) {
        Optional<CourierStoreEntranceLogEntity> courierStoreEntranceLogEntityOptional = courierStoreEntranceLogJpaRepository.findByCourierIdAndStoreIdOrderByEnteredAtDesc(courierId, storeId);
        if (courierStoreEntranceLogEntityOptional.isPresent()) {
            CourierStoreEntranceLogEntity courierStoreEntranceLogEntity = courierStoreEntranceLogEntityOptional.get();
            if (courierStoreEntranceLogEntity.getEnteredAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
                courierStoreEntranceLogEntity.setEnteredAt(LocalDateTime.now());
                return true;
            }
        }
        return false;
    }
}
