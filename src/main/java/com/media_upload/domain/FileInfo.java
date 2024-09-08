package com.media_upload.domain;

import java.io.Serializable;

public class FileInfo implements Serializable{

	String fileUrl;
	String fileName;
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
