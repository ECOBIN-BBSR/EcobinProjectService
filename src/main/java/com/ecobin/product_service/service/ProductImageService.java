package com.ecobin.product_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.Set;

@Service
public class ProductImageService {

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "webp");

    private final S3Presigner s3Presigner;

    @Value("${storage.bucket}")
    private String bucketName;

    public ProductImageService(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    // Extract file extension
    public String getExtension(MultipartFile file) {

        String filename = file.getOriginalFilename();

        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        return filename.substring(
                filename.lastIndexOf(".") + 1
        ).toLowerCase();
    }

    // Validate uploaded image
    public void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image cannot be empty");
        }

        String extension = getExtension(file);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Only JPG, JPEG, PNG and WEBP images are allowed"
            );
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "Image size cannot exceed 5 MB"
            );
        }
    }

    // Generate temporary URL to view an image
    public String generatePresignedUrl(String objectKey) {

        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .getObjectRequest(getObjectRequest)
                        .build();

        return s3Presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }
}