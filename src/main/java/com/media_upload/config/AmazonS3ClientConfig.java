package com.media_upload.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/*
 * DefaultAWSCredentialsProviderChain will be used for Cloud deployments 
 * to fetch credentials from EC2 Instance profile to connect to AWS S3
 */
@Configuration
public class AmazonS3ClientConfig {

    @Value("${aws.secret-key:}")
    private String aws_secret_key;

    @Value("${aws.access-key:}")
    private String aws_access_key;

    @Value("${aws.account-id:}")
    private String aws_account_id;
    
    @Value("${s3.bucket.vpc-endpoint.id}")
    private String s3vpcesecret;
    
    @Autowired
    SecretsManagerClient secretClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3ClientConfig.class); 
    
    @Profile("cloud")
    @Bean
    AmazonS3 s3ClientCloud() {
    	LOGGER.info("Build credentials for cloud");
    	return AmazonS3ClientBuilder.standard()
    			.withEndpointConfiguration(
						new AwsClientBuilder.EndpointConfiguration(
								getSecretValue(s3vpcesecret), Regions.AP_SOUTH_1.getName()))
				.build();
    }

    @Profile("dev")
    @Bean
    AmazonS3 s3Client() {
    	LOGGER.info("Build credentials for Dev");
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(getCredentials()))
                .withRegion(Region.AP_SOUTH_1.toString())
                .build();
    }

    private AWSCredentials getCredentials(){
    	LOGGER.info("Inside getCredentials");
        return new BasicAWSCredentials(aws_access_key,aws_secret_key,aws_account_id);
    }
    
    private String getSecretValue(String secretName) {
		GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        LOGGER.info("Fetch value for secret = {}",secretName);
        return valueResponse.secretString();
	}
}
