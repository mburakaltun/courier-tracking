package com.mburakaltun.couriertracking.common.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiExceptionResponse {
    private String errorMessage;
}
