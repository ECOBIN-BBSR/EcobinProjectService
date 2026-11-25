package com.ecobin.product_service.storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
public class NeonObjectStorageService
        implements ObjectStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${storage.bucket}")
    private String bucket;

    public NeonObjectStorageService(
            S3Client s3Client,
            S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public String upload(
            byte[] file,
            String objectKey,
            String contentType) {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(file)
        );

        return objectKey;
    }

    @Override
    public void delete(String objectKey) {

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .build()
        );
    }

    @Override
    public String generateDownloadUrl(String objectKey) {

        GetObjectRequest getRequest =
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .getObjectRequest(getRequest)
                        .build();

        return s3Presigner.presignGetObject(
                presignRequest
        ).url().toString();
    }
}