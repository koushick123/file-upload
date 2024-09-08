package com.media_upload.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Configuration
public class AWSRDSDataSourceConfig {

	@Value("${spring.datasource.username}")
	private String username;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${spring.datasource.url}")
	private String url;

	@Profile("dev")
	@Bean
	public DataSource dataSourceForDev() 
	 {
		DataSourceBuilder<?> builder = DataSourceBuilder.create();
	    builder.username(username);
	    builder.password(password);
	    builder.url(url);
	    return builder.build();
	}
	
	@Bean("secretClient")
	public SecretsManagerClient getSecretsClient() {
		return SecretsManagerClient.builder()
                .region(Region.AP_SOUTH_1)
                .build();
	}
	
	@Autowired
	@Qualifier("secretClient")
	private SecretsManagerClient awsSecretClient;
				
	@Profile("cloud")
	@Bean
	public DataSource dataSourceForCloud() {
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

        GetSecretValueResponse valueResponse = awsSecretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
	}
}
