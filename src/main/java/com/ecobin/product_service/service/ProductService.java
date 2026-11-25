package com.ecobin.product_service.service;
import com.ecobin.product_service.dto.ProductRequest;
import com.ecobin.product_service.dto.ProductResponse;
import com.ecobin.product_service.entity.Product;
import com.ecobin.product_service.entity.ProductImage;
import com.ecobin.product_service.enums.ProductCategory;
import com.ecobin.product_service.enums.ProductStatus;
import com.ecobin.product_service.repository.ProductImageRepository;
import com.ecobin.product_service.repository.ProductRepository;
import com.ecobin.product_service.storage.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final ObjectStorageService storageService;
    private final ProductImageService productImageService;

    @Transactional
    public Product createProduct(
            ProductRequest request,
            List<MultipartFile> files,
            UUID ownerId) throws IOException {

        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(request.category());
        product.setBrand(request.brand());
        product.setModel(request.model());
        product.setCondition(request.condition());
        product.setQuantity(request.quantity());
        product.setOwnerId(ownerId);
        product.setStatus(ProductStatus.AVAILABLE);

        // Save first to obtain the product ID
        product = productRepository.save(product);

        List<String> uploadedKeys = new ArrayList<>();

        try {

            for (MultipartFile file : files) {

//                validateImage(file);

                String extension = productImageService.getExtension(file);

                String objectKey =
                        "products/" + product.getId()
                                + "/" + UUID.randomUUID()
                                + "." + extension;

                storageService.upload(
                        file.getBytes(),
                        objectKey,
                        file.getContentType()
                );

                uploadedKeys.add(objectKey);

                ProductImage image = new ProductImage();

                image.setProduct(product);
                image.setObjectKey(objectKey);
                image.setOriginalFilename(file.getOriginalFilename());
                image.setContentType(file.getContentType());

                imageRepository.save(image);
            }

            return product;

        } catch (Exception exception) {

            // Compensate for uploaded files if DB operation fails
            for (String key : uploadedKeys) {
                try {
                    storageService.delete(key);
                } catch (Exception cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }

            throw new RuntimeException(
                    "Product upload failed", exception
            );
        }
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        return mapToProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        List<Product> products = productRepository.findAllWithImages();

        return products.stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    private ProductResponse mapToProductResponse(Product product) {

        List<String> imageUrls = product.getImages()
                .stream()
                .map(image -> productImageService
                        .generatePresignedUrl(image.getObjectKey()))
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getBrand(),
                product.getModel(),
                product.getCondition(),
                product.getQuantity(),
                product.getStatus(),
                imageUrls,
                product.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(ProductCategory category) {

        List<Product> products = productRepository.findByCategory(category);

        return products.stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByOwnerId(UUID ownerId) {

        List<Product> products = productRepository.findByOwnerId(ownerId);

        return products.stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Transactional
    public ProductResponse updateProductStatus(
            UUID productId,
            ProductStatus status
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus(status);

        productRepository.save(product);

        return mapToProductResponse(product);
    }
}


