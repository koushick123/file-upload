package com.media_upload.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mediaupload/")
public class LogsController {
    
	@Value("${logfile.location}")
	private String logFileLocation; 
        
    @GetMapping("/logs")
    public String getLogs() throws IOException {
    	StringBuffer fileContent = new StringBuffer();

        try (BufferedReader br = new BufferedReader(new FileReader(logFileLocation))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileContent.append(line).append("<br>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent.toString(); 
    }   
}