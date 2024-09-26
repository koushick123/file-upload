package com.media_upload.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = {"com.media_upload"})
@EnableJpaRepositories(basePackages = {"com.media_upload.repository"})
@EntityScan(basePackages = {"com.media_upload.domain"})
public class UploadServiceApplication {

	public static void main(String[] args) {
		System.setProperty("aws.java.v1.disableDeprecationAnnouncement", "true");
		SpringApplication.run(UploadServiceApplication.class, args);
	}

}
