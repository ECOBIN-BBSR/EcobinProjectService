package com.ecobin.product_service.storage;

public interface ObjectStorageService {

    String upload(
            byte[] file,
            String objectKey,
            String contentType
    );

    void delete(String objectKey);

    String generateDownloadUrl(String objectKey);
}