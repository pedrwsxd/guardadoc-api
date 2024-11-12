package com.spring.guardadoc.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("SUA_ACCESS_KEY", "SUA_SECRET_KEY");
        return AmazonS3ClientBuilder.standard()
                .withRegion() // Insira a região do seu bucket
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}