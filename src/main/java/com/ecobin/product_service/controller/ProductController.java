package com.ecobin.product_service.controller;

import com.ecobin.product_service.dto.ProductRequest;
import com.ecobin.product_service.dto.ProductResponse;
import com.ecobin.product_service.dto.UpdateProductStatusRequest;
import com.ecobin.product_service.entity.Product;
import com.ecobin.product_service.enums.ProductCategory;
import com.ecobin.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(

            @Valid @RequestPart("product")
            ProductRequest request,

            @RequestPart("images")
            List<MultipartFile> images

    ) throws IOException {

        // Replace with authenticated JWT user ID
        UUID ownerId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        Product product = productService.createProduct(
                request,
                images,
                ownerId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(product);
    }

    @GetMapping("/{id:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }


    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable ProductCategory category) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(category)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductResponse>> getProductsByUser(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                productService.getProductsByOwnerId(userId)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponse> updateProductStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductStatusRequest request) {

        return ResponseEntity.ok(
                productService.updateProductStatus(id, request.status())
        );
    }
}
