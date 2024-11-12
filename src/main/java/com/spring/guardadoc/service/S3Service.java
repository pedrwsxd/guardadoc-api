package com.spring.guardadoc.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${guarddoc.bucket.name}")
    private String bucketName;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFile(String fileName, byte[] fileData) {
        String objectKey = "uploads/" + fileName;

        amazonS3.putObject(new PutObjectRequest(bucketName, objectKey, new ByteArrayInputStream(fileData), null));

        return objectKey;
    }

    public byte[] downloadFile(String fileKey) throws IOException {
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, fileKey));
        InputStream inputStream = s3Object.getObjectContent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }
}