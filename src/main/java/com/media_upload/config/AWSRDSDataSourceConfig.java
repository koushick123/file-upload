package com.media_upload.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.media_upload.service.UploadService;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
/**
 * Same configuration and connection method will be used in Dev and Cloud for 
 * connecting to AWS RDS.
 * 
 * There is variation in connecting to AWS Secrets manager in Dev and Cloud.
 * Check getSecretsClient() for additional details.
 */
@Configuration
public class AWSRDSDataSourceConfig {

	@Value("${spring.datasource.username}")
	private String username;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${spring.datasource.url}")
	private String url;
	
	@Value("${aws.secret-key:}")
    private String aws_secret_key;

    @Value("${aws.access-key:}")
    private String aws_access_key;
    
    @Autowired
    Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSRDSDataSourceConfig.class);   
    
	private SecretsManagerClient secretClient = null;
	
    SecretsManagerClient getSecretsClient() {
    	if(secretClient == null) {
    		if(env.getActiveProfiles()[0].equals("dev")) {
    			LOGGER.info("Set access and secret keys for Dev");
    			//This is needed for Dev profile env since I will be using Instance profiles approach for Cloud profile
	    		System.setProperty("aws.accessKeyId", aws_access_key);
	    		System.setProperty("aws.secretAccessKey", aws_secret_key);
    		}
			secretClient = SecretsManagerClient.builder()
                    .region(Region.AP_SOUTH_1)
                    .build();
    	}
		return secretClient;
	}
	
    @Bean
    DataSource dataSource() {
    	LOGGER.info("Create Datasource");
		DataSourceBuilder<?> builder = DataSourceBuilder.create();
	    builder.username(getSecretValue(username));
		builder.password(getSecretValue(password));
	    builder.url(getSecretValue(url));
	    return builder.build();
	}
	
	private String getSecretValue(String secretName) {
		GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = getSecretsClient().getSecretValue(valueRequest);
        LOGGER.info("Fetch value for secret = {}",secretName);
        return valueResponse.secretString();
	}
}
