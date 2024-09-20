package com.media_upload.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.media_upload.domain.FileInfo;
import com.media_upload.service.UploadService;

@RestController
@RequestMapping("/mediaupload/")
public class UploadController {
    @Autowired
    private UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) {
        String uploadId = uploadService.uploadFile(file);
        return ResponseEntity.ok(uploadId);
    }

    @GetMapping("/health-check")
    public String healthCheck() {
        return "Healthy";
    }
    
    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getFiles() {
    	return ResponseEntity.ok(uploadService.getFiles());
    }
}