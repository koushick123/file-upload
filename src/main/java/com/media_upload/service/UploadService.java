package com.media_upload.service;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.media_upload.domain.FileInfo;
import com.media_upload.domain.FileTable;
import com.media_upload.repository.FileUploadRepository;
import com.media_upload.uploadstatus.UploadStatus;

@Service
public class UploadService {
    
    @Autowired
    FileUploadRepository fileUploadRepo;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);   

    @Autowired
    @Qualifier("awsS3client")
    AmazonS3 s3client;
    
    @Value("${s3.bucket.name}")
    private String s3BucketName; 
    
    @Autowired
    @Qualifier("awsDynamoDBClient")
    DynamoDB dynamoDB;

    public String uploadFile(MultipartFile file) {
        String uploadId = UUID.randomUUID().toString();
        FileTable fileUploadStatus = new FileTable();

        try {
        	int fileId = RandomGenerator.getDefault().nextInt();
        	String fileName = file.getOriginalFilename();
        	//Update RDS            
            fileUploadStatus.setFileId(Integer.valueOf(fileId));
            fileUploadStatus.setFileName(fileName);
            
        	//Upload to AWS S3
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            s3client.putObject(s3BucketName, file.getOriginalFilename(), file.getInputStream(), objectMetadata);
            LOGGER.info("Sucessfully uploaded to AWS S3 {} ",file.getOriginalFilename());
            //Update Dynamo DB with Metadata
            String tableName = "upload";
            Table table = dynamoDB.getTable(tableName);

            Item item = new Item();
            item.withPrimaryKey("file-id",fileId);
            item.withString("file-name", fileName);
            item.withNumber("file-size", file.getSize());
            item.withString("file-type", fileName.substring(fileName.indexOf(".")+1));
            
            table.putItem(item);
            LOGGER.info("Successfully uploaded to DynamoDB");
            
            fileUploadStatus.setFileUploadStatus(UploadStatus.SUCCESS.toString());
        } catch (AmazonServiceException e) {
            // Handle exceptions
            LOGGER.error("Exception during upload = {}",e.getMessage());
            fileUploadStatus.setFileUploadStatus(UploadStatus.FAIL.toString());
            fileUploadStatus.setFileUploadErrDesc(e.getMessage().substring(0, 500));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileUploadRepo.save(fileUploadStatus);
        LOGGER.info("File status Updated");
        return uploadId;
    }
    
    public List<FileInfo> getFiles(){
    	ListObjectsV2Result s3Objects = s3client.listObjectsV2(s3BucketName);
    	return s3Objects.getObjectSummaries().stream().map(s3obj -> {
    		FileInfo fileinfo = new FileInfo();
    		fileinfo.setFileName(s3obj.getKey());
    		URL s3Url = s3client.getUrl(s3BucketName, s3obj.getKey());
    		StringBuilder fileUrl = new StringBuilder("");
    		fileUrl.append(s3Url.getProtocol());
    		fileUrl.append("://");
    		fileUrl.append(s3Url.getHost());
    		fileUrl.append(":");
    		fileUrl.append(s3Url.getPort());
    		fileUrl.append(s3Url.getPath());
    		fileinfo.setFileUrl(fileUrl.toString());
    		return fileinfo;
    	}).collect(Collectors.toList());
    }
}
