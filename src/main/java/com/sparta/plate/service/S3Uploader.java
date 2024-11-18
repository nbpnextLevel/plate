package com.sparta.plate.service;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Uploader {

    private static final String IMAGE_UPLOAD_DIR = "product";

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = IMAGE_UPLOAD_DIR + "/" + UUID.randomUUID() + extension;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());

            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (Exception e) {
            log.error("Error occurred during file upload: {}", e.getMessage(), e);
            throw new RuntimeException("File upload to S3 failed", e);
        }
    }

    private String upload(File uploadFile) {
        String fileName = IMAGE_UPLOAD_DIR + "/" + UUID.randomUUID();
        try {
            String uploadImageUrl = putS3(uploadFile, fileName);
            removeNewFile(uploadFile);
            return uploadImageUrl;
        } catch (Exception e) {
            log.error("Error occurred during file upload: {}", e.getMessage(), e); // 상세한 로그 출력
            throw new RuntimeException("File upload to S3 failed", e);
        }
    }

    private String putS3(File uploadFile, String fileName) {
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
        } else {
            log.error("File delete failed: {}", targetFile.getAbsolutePath());
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File directory = new File(IMAGE_UPLOAD_DIR);
        if (!directory.exists() && !directory.mkdirs()) {
            log.error("Failed to create directory: {}", IMAGE_UPLOAD_DIR);
            throw new IOException("Failed to create directory");
        }

        File convertFile = new File(directory, Objects.requireNonNull(file.getOriginalFilename()));
        if (convertFile.exists()) {
            log.error("File already exists: {}", convertFile.getAbsolutePath());
            throw new IOException("File already exists: " + convertFile.getAbsolutePath());
        }

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        } else {
            log.error("Failed to create file: {}", convertFile.getAbsolutePath());
            return Optional.empty();
        }
    }

}