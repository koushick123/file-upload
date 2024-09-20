package com.media_upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

/*
 * DefaultAWSCredentialsProviderChain will be used for Cloud deployments 
 * to fetch credentials from EC2 Instance profile to connect to Dynamo DB
 */
@Configuration
public class AmazonDynamodBClientConfig {
	
    @Value("${aws.secret-key:}")
    private String aws_secret_key;

    @Value("${aws.access-key:}")
    private String aws_access_key;

    @Value("${aws.account-id:}")
    private String aws_account_id;
    
	public AWSCredentials getCredentials(){
        System.out.println("Inside getCredentials");
        return new BasicAWSCredentials(aws_access_key,aws_secret_key,aws_account_id);
    }

	@Profile("dev")
	@Bean("awsDynamoDBClient")
    DynamoDB amazonDynamoDBForDev() {
    	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
    			.withCredentials(new AWSStaticCredentialsProvider(getCredentials()))
    			.withRegion("ap-south-1")
    			.build();
    	return new DynamoDB(client);
    }
	
	@Profile("cloud")
	@Bean("awsDynamoDBClient")
    DynamoDB amazonDynamoDBForCloud() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new DefaultAWSCredentialsProviderChain())
				.build();
		return new DynamoDB(client);
	}
}
