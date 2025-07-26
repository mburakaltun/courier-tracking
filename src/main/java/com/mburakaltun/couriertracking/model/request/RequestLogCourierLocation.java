package com.mburakaltun.couriertracking.model.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestLogCourierLocation {
    @NotNull(message = "{validation.courierId.notNull}")
    private Long courierId;

    @NotNull(message = "{validation.recordedAt.notNull}")
    @PastOrPresent(message = "{validation.recordedAt.pastOrPresent}")
    private LocalDateTime recordedAt;

    @NotNull
    @DecimalMin(value = "-90.0", message = "{validation.latitude.min}")
    @DecimalMax(value = "90.0", message = "{validation.latitude.max}")
    private Double latitude;

    @NotNull
    @DecimalMin(value = "-180.0", message = "{validation.longitude.min}")
    @DecimalMax(value = "180.0", message = "{validation.longitude.max}")
    private Double longitude;
}
