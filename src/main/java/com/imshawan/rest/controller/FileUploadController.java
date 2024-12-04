package com.imshawan.rest.controller;

import com.imshawan.rest.response.ApiResponse;
import com.imshawan.rest.service.FileUploaderService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files/upload")

public class FileUploadController {
    
    @Autowired
    private FileUploaderService fileUploaderService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Map<String, String> fileInfo = fileUploaderService.uploadFile(file);
        ApiResponse apiResponse = new ApiResponse(request, fileInfo);
        apiResponse.setMessage("File uploaded successfully");
        
        return ResponseEntity.ok().body(apiResponse);
    }
}
