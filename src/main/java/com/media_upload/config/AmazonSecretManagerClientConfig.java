package com.media_upload.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonSecretManagerClientConfig.class);
	
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
