package com.ecobin.product_service.repository;
import com.ecobin.product_service.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface ProductImageRepository
        extends JpaRepository<ProductImage, UUID> {

    List<ProductImage> findByProductId(UUID productId);
}
