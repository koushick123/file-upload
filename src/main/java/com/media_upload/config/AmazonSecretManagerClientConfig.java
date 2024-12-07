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
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Configuration
public class AmazonSecretManagerClientConfig {
	
	@Autowired
    SecretsManagerClient secretClient;
	
	@Value("${secret-manager.vpc-endpoint.dns}")
    private String secretManagerVpce;
	
	@Value("${aws.secret-key:}")
    private String aws_secret_key;

    @Value("${aws.access-key:}")
    private String aws_access_key;

    @Value("${aws.account-id:}")
    private String aws_account_id;
    
    @Value("${sns.vpc-endpoint.id}")
    private String snsVpcEndpoint;
    
	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonSecretManagerClientConfig.class);
	
	 private AWSCredentials getCredentials(){
	    	LOGGER.info("Inside getCredentials");
	        return new BasicAWSCredentials(aws_access_key,aws_secret_key,aws_account_id);
	    }
	 
	@Profile("dev")
	@Bean
	AWSSecretsManager getSecretClientDev() {
		LOGGER.info("Create AWS SecretManager Client");
    	return AWSSecretsManagerClientBuilder.standard()
    			.withRegion(Regions.AP_SOUTH_1)
                .withCredentials(new AWSStaticCredentialsProvider(getCredentials()))
    	.build();
	}
	
	@Profile("cloud")
	@Bean
    AWSSecretsManager getSecretClient() {
		LOGGER.info("Create AWS SecretManager Client");
    	return AWSSecretsManagerClientBuilder.standard()
    	.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
								secretManagerVpce, Regions.AP_SOUTH_1.getName()))
    	.build();
    }
}
