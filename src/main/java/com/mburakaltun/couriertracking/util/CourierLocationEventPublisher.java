package com.mburakaltun.couriertracking.util;

import com.mburakaltun.couriertracking.model.entity.CourierLocationEntity;
import com.mburakaltun.couriertracking.model.event.CourierLocationEvent;
import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourierLocationEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @PrePersist
    public void publishCourierLocationEvent(CourierLocationEntity courierLocationEntity) {
        applicationEventPublisher.publishEvent(new CourierLocationEvent(courierLocationEntity));
    }
}
