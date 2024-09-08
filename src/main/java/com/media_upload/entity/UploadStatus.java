package com.media_upload.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "upload_status")
public class UploadStatus {
    @DynamoDBHashKey
    private String uploadId;

    @DynamoDBAttribute
    private String status;
}
