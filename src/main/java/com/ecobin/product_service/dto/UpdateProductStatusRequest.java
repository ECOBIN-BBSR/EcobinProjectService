package com.ecobin.product_service.dto;

import com.ecobin.product_service.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateProductStatusRequest(
        @NotNull ProductStatus status
) {}