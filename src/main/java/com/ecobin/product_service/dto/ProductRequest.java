package com.ecobin.product_service.dto;

import com.ecobin.product_service.enums.ProductCategory;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        ProductCategory category,

        String brand,

        String model,

        @NotBlank
        String condition,

        @NotNull
        @Min(1)
        Integer quantity

) {}