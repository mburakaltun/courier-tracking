package com.mburakaltun.couriertracking.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreateCourier {
    @NotBlank(message = "{validation.courier.name.notBlank}")
    private String name;
}
