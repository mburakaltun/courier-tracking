package com.mburakaltun.couriertracking.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestQueryCourierTotalDistance {
    @NotNull(message = "{validation.courierId.notNull}")
    private Long courierId;
}
