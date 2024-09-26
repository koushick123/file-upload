package com.media_upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

/**
 * DefaultAWSCredentialsProviderChain will be used for Cloud deployments 
 * to fetch credentials from EC2 Instance profile to connect to AWS SNS
 */
@Configuration
public class AWSSNSConfig {
	
	@Value("${aws.secret-key:}")
    private String aws_secret_key;

    @Value("${aws.access-key:}")
    private String aws_access_key;

    @Value("${aws.account-id:}")
    private String aws_account_id;

    private AWSCredentials getCredentials(){
        System.out.println("Inside getCredentials");
        return new BasicAWSCredentials(aws_access_key,aws_secret_key,aws_account_id);
    }
    
    @Profile("dev")
    @Bean
    AmazonSNS SNSClientForDev() {
    	// Create SNS client
        AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                .withRegion(Regions.AP_SOUTH_1)
                .withCredentials(new AWSStaticCredentialsProvider(getCredentials()))
                .build();
    	return snsClient;
    }
    
    @Profile("cloud")
    @Bean
    AmazonSNS SNSClientForCloud() {
    	// Create SNS client
        AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                .withRegion(Regions.AP_SOUTH_1)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    	return snsClient;
    }
}
