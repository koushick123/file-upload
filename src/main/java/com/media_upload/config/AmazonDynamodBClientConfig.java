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
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

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
    
    @Value("${dynamodb.vpc-endpoint.id}")
    private String dynamodbvpcesecret;
    
    @Autowired
    SecretsManagerClient secretClient;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonDynamodBClientConfig.class); 
    
	public AWSCredentials getCredentials(){
		LOGGER.info("Inside getCredentials");
        return new BasicAWSCredentials(aws_access_key,aws_secret_key,aws_account_id);
    }

	@Profile("dev")
	@Bean
    DynamoDB amazonDynamoDBForDev() {
    	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
    			.withCredentials(new AWSStaticCredentialsProvider(getCredentials()))
    			.withRegion(Region.AP_SOUTH_1.toString())
    			.build();
    	return new DynamoDB(client);
    }
	
	@Profile("cloud")
	@Bean
    DynamoDB amazonDynamoDBForCloud() {
		AmazonDynamoDB dynamodb = AmazonDynamoDBClientBuilder.standard()
				.withEndpointConfiguration(
						new AwsClientBuilder.EndpointConfiguration(
								getSecretValue(dynamodbvpcesecret), Regions.AP_SOUTH_1.getName()))
				.build();
		return new DynamoDB(dynamodb);
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
