package com.ecobin.product_service.dto;
import com.ecobin.product_service.enums.ProductStatus;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(

        UUID id,
        String name,
        String description,
        com.ecobin.product_service.enums.ProductCategory category,
        String brand,
        String model,
        String condition,
        Integer quantity,
        ProductStatus status,
        List<String> imageUrls,
        LocalDateTime createdAt

) {}