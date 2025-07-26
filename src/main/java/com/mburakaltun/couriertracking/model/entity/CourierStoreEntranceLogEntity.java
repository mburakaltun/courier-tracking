package com.mburakaltun.couriertracking.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "courier_store_entrance_log")
public class CourierStoreEntranceLogEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entered_at", nullable = false)
    private LocalDateTime enteredAt;

    @Column(name = "last_recorded_at", nullable = false)
    private LocalDateTime lastRecordedAt;

    @Column(name = "courier_id", nullable = false)
    private Long courierId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;
}
