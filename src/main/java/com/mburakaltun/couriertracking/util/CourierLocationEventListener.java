package com.mburakaltun.couriertracking.util;

import com.mburakaltun.couriertracking.model.entity.CourierEntity;
import com.mburakaltun.couriertracking.model.entity.CourierLocationEntity;
import com.mburakaltun.couriertracking.model.event.CourierLocationEvent;
import com.mburakaltun.couriertracking.repository.CourierJpaRepository;
import com.mburakaltun.couriertracking.repository.CourierLocationJpaRepository;
import com.mburakaltun.couriertracking.service.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourierLocationEventListener {

    private final CourierJpaRepository courierJpaRepository;
    private final CourierLocationJpaRepository courierLocationJpaRepository;
    private final DistanceCalculator distanceCalculator;

    @EventListener
    public void handleCourierLocationEvent(CourierLocationEvent event) {
        CourierLocationEntity courierLocationEntity = event.getCourierLocationEntity();
        Long courierId = courierLocationEntity.getCourierId();
        Double latitude = courierLocationEntity.getLatitude();
        Double longitude = courierLocationEntity.getLongitude();

        CourierEntity courierEntity = courierJpaRepository.findById(courierId)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found with ID: " + courierId));

        List<CourierLocationEntity> courierLocations = courierLocationJpaRepository.findByCourierIdOrderByRecordedAtDesc(courierId);

        if (!courierLocations.isEmpty()) {
            CourierLocationEntity lastLocation = courierLocations.get(0);
            double distance = distanceCalculator.calculateDistanceInMeters(lastLocation.getLatitude(), lastLocation.getLongitude(), latitude, longitude);
            log.info("Courier ID: {}, Distance from last location: {} meters", courierId, distance);
            courierEntity.setTotalDistance(courierEntity.getTotalDistance() + distance);
        }

        courierJpaRepository.save(courierEntity);
    }
}
