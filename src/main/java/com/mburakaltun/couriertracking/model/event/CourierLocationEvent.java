package com.mburakaltun.couriertracking.model.event;

import com.mburakaltun.couriertracking.model.entity.CourierLocationEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CourierLocationEvent extends ApplicationEvent {
    private final CourierLocationEntity courierLocationEntity;

    public CourierLocationEvent(CourierLocationEntity courierLocationEntity) {
        super(courierLocationEntity);
        this.courierLocationEntity = courierLocationEntity;
    }
}
