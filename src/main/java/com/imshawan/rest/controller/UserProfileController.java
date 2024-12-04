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

@RestController
@RequestMapping("/api/users/profile")
public class UserProfileController {

    @Autowired
    private FileUploaderService fileUploaderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/picture")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable String userId, @RequestParam("file") MultipartFile file,
            Authentication authentication, HttpServletRequest request) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User loggedInUser = (User) principal;
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
