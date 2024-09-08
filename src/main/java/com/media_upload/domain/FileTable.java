package com.media_upload.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FILE_TABLE")
public class FileTable {

	@Column(name = "FILE_ID")
	@Id
	Integer fileId;
	
	@Column(name = "FILE_NAME")
	String fileName;
	
	@Column(name = "FILE_UPLOAD_STATUS")
	String fileUploadStatus;
	
	@Column(name = "FILE_UPLOAD_ERR_DESC")
	String fileUploadErrDesc;

	public Integer getFileId() {
		return fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileUploadStatus() {
		return fileUploadStatus;
	}

	public void setFileUploadStatus(String fileUploadStatus) {
		this.fileUploadStatus = fileUploadStatus;
	}

	public String getFileUploadErrDesc() {
		return fileUploadErrDesc;
	}

	public void setFileUploadErrDesc(String fileUploadErrDesc) {
		this.fileUploadErrDesc = fileUploadErrDesc;
	}
}
