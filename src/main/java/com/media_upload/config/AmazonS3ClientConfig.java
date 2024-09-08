package com.media_upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Component
public class AmazonS3ClientConfig {

    @Value("${aws.secret-key:}")
    private String aws_secret_key;

    @Value("${aws.access-key:}")
    private String aws_access_key;

    @Value("${aws.account-id:}")
    private String aws_account_id;

    @Profile("cloud")
    @Bean("awsS3client")
    public AmazonS3 s3ClientCloud() {
        System.out.println("Build credentials for cloud");
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    @Profile("dev")
    @Bean("awsS3client")
    public AmazonS3 s3Client() {
        System.out.println("Build credentials for Dev");
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(getCredentials()))
                .withRegion("ap-south-1")
                .build();
    }

    private AWSCredentials getCredentials(){
        System.out.println("Inside getCredentials");
        return new BasicAWSCredentials(aws_access_key,aws_secret_key,aws_account_id);
    }
}
