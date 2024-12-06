package com.imshawan.rest.controller;

import com.imshawan.rest.exceptions.EntityNotFoundException;
import com.imshawan.rest.model.User;
import com.imshawan.rest.service.FileUploaderService;
import com.imshawan.rest.service.UserService;
import com.imshawan.rest.repository.UserRepository;
import com.imshawan.rest.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;


@RestController
@RequestMapping("/api/users/profile")
@Tag(name = "User Profile APIs", description = "Endpoints for managing user profiles including uploading profile pictures.")
public class UserProfileController {

    @Autowired
    private FileUploaderService fileUploaderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/picture")
    @Operation(
            summary = "Upload a profile picture",
            description = "Allows an authenticated user to upload a profile picture for their account. Admins can upload for any user.",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user whose profile picture is being updated", required = true),
                    @Parameter(name = "file", description = "Multipart file containing the profile picture to be uploaded", required = true)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Profile picture uploaded successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "Access denied. User is not authorized to update this profile picture",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> uploadProfilePicture(@PathVariable String userId, @RequestParam("file") MultipartFile file,
            Authentication authentication, HttpServletRequest request) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof User loggedInUser) {
            if (!loggedInUser.getId().equals(userId) && !userService.hasAdminAuthority(authentication)) {
                throw new AccessDeniedException("You are not authorized to update this profile picture.");
            }
        } else {
            throw new AccessDeniedException("User not found or not authenticated");
        }

        Map<String, String> fileInfo = fileUploaderService.uploadFile(file);

        // Fetch the user and update the profile picture URL
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
        user.setProfilePicture(fileInfo.get("fileUrl"));
        userRepository.save(user);

        ApiResponse apiResponse = new ApiResponse(request, fileInfo);
        apiResponse.setMessage("Profile picture uploaded successfully");

        return ResponseEntity.ok().body(apiResponse);
    }
}
