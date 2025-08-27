//package com.kh.service;
//
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Service
//public class S3Service {
//
//    private final AmazonS3 amazonS3;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucketName;
//
//    public S3Service(AmazonS3 amazonS3) {
//        this.amazonS3 = amazonS3;
//    }
//
//    // 파일 업로드
//    public String upload(MultipartFile file) {
//        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//        try {
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentLength(file.getSize());
//            metadata.setContentType(file.getContentType());
//
//            // S3에 업로드
//            amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
//
//            // 업로드된 파일의 URL 리턴
//            return amazonS3.getUrl(bucketName, fileName).toString();
//
//        } catch (IOException e) {
//            throw new RuntimeException("S3 업로드 실패", e);
//        }
//    }
//}