package com.ecobin.product_service.repository;

import com.ecobin.product_service.entity.Product;
import com.ecobin.product_service.enums.ProductCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, UUID> {

    @EntityGraph(attributePaths = "images")
    @Query("SELECT DISTINCT p FROM Product p")
    List<Product> findAllWithImages();

    @EntityGraph(attributePaths = "images")
    List<Product> findByOwnerId(UUID ownerId);

    @EntityGraph(attributePaths = "images")
    @Query("SELECT DISTINCT p FROM Product p WHERE p.category = :category")
    List<Product> findByCategory(
            @Param("category") ProductCategory category
    );

    List<Product> findByStatus(String status);

    Product getProductById(UUID id);
}