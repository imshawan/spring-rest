package com.imshawan.rest.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileUploaderService {

    private static final String UPLOAD_DIR = "uploads";

    public Map<String, String> uploadFile(MultipartFile file) {
        try {
            // Ensure the uploads directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "-" + originalName;
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            Map<String, String> fileMeta = new HashMap<>();

            fileMeta.put("fileUrl", "/uploads/" + fileName);
            fileMeta.put("fileName", originalName);

            return fileMeta;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }
}
