package com.imshawan.rest.controller;

import com.imshawan.rest.dto.UserUpdateRequest;
import com.imshawan.rest.exceptions.EntityNotFoundException;
import com.imshawan.rest.model.User;
import com.imshawan.rest.response.HTTPError;
import com.imshawan.rest.service.FileUploaderService;
import com.imshawan.rest.service.UserService;
import com.imshawan.rest.repository.UserRepository;
import com.imshawan.rest.response.ApiResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@SecurityRequirement(name = "bearerAuth")
public class UserProfileController {

    @Autowired
    private FileUploaderService fileUploaderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user profile",
            description = "Fetches the user profile based on the provided user ID.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "User profile found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "User not found")
    })
    public ResponseEntity<User> getUserProfile(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update user profile",
            description = "Updates the user profile for the specified user ID. Only non-essential fields such as fullname and profile picture can be modified. Critical fields (username, email, password, authorities) are immutable by this API call. The request requires the user to either be the owner of the profile or have admin authority.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "User profile updated successfully",
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
                                    schema = @Schema(implementation = HTTPError.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HTTPError.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "Access denied. User is not authorized to update this profile.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HTTPError.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> updateUserProfile(@PathVariable String id, @Valid @RequestBody UserUpdateRequest user, HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        HTTPError httpError = new HTTPError(request, response);

        if (principal instanceof User loggedInUser) {
            if (!loggedInUser.getId().equals(id) && !userService.hasAdminAuthority(authentication)) {
                throw new AccessDeniedException("You are not authorized to update this profile picture.");
            }
        } else {
            throw new AccessDeniedException("User not found or not authenticated");
        }

        try {
            User updatedUser = userService.updateUserDataById(id, user);
            ApiResponse apiResponse = new ApiResponse(request, updatedUser);
            apiResponse.setMessage("User details updated successfully");

            return ResponseEntity.ok().body(apiResponse);
        } catch (EntityNotFoundException ex) {
            httpError.setStatus(HttpStatus.NOT_FOUND.value());
            httpError.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(httpError);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user profile",
            description = "Deletes the user profile for the specified user ID. Only the user themselves or an admin can delete a profile. If the user is not found, an error will be returned.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "204",
                            description = "User profile deleted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HTTPError.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "Access denied. User is not authorized to delete this profile.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HTTPError.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> deleteUserProfile(@PathVariable String id, HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        HTTPError httpError = new HTTPError(request, response);
        Object principal = authentication.getPrincipal();

        if (principal instanceof User loggedInUser) {
            if (!loggedInUser.getId().equals(id) && !userService.hasAdminAuthority(authentication)) {
                throw new AccessDeniedException("You are not authorized to update this profile picture.");
            }
        } else {
            throw new AccessDeniedException("User not found or not authenticated");
        }

        try {
            userService.deleteUserById(id);
            ApiResponse apiResponse = new ApiResponse(request, null);
            apiResponse.setMessage("User account was deleted successfully");

            return ResponseEntity.ok().body(apiResponse);
        } catch (EntityNotFoundException ex) {
            httpError.setStatus(HttpStatus.NOT_FOUND.value());
            httpError.setMessage(ex.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(httpError);
        }
    }

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
