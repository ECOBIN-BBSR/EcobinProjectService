package com.ecobin.product_service.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mesage,
        String path,
        Map<String,String> validationError
) {
}
