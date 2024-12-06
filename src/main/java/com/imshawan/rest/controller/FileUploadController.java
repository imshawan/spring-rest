package com.imshawan.rest.controller;

import com.imshawan.rest.response.ApiResponse;
import com.imshawan.rest.service.FileUploaderService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/files/upload")
@Tag(name = "File Upload APIs", description = "Endpoints for uploading files to the server")
public class FileUploadController {
    
    @Autowired
    private FileUploaderService fileUploaderService;

    @PostMapping
    @Operation(
            summary = "Upload a file",
            description = "Uploads a file to the server and returns information about the uploaded file, such as its URL.",
            parameters = {
                    @Parameter(
                            name = "file",
                            description = "Multipart file to be uploaded",
                            required = true,
                            schema = @Schema(type = "string", format = "binary")
                    )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "File uploaded successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid file or upload failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Map<String, String> fileInfo = fileUploaderService.uploadFile(file);
        ApiResponse apiResponse = new ApiResponse(request, fileInfo);
        apiResponse.setMessage("File uploaded successfully");
        
        return ResponseEntity.ok().body(apiResponse);
    }
}
