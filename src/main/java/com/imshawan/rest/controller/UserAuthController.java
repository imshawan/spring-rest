package com.imshawan.rest.controller;

import com.imshawan.rest.dto.SigninRequest;
import com.imshawan.rest.dto.UserRegistrationRequest;
import com.imshawan.rest.model.User;
import com.imshawan.rest.response.ApiResponse;
import com.imshawan.rest.response.HTTPError;
import com.imshawan.rest.security.JwtUtil;
import com.imshawan.rest.service.UserService;
import com.imshawan.rest.utils.EmailUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Optional;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users onboarding & Authentication APIs", description = "Endpoints for users registration and signing in")
public class UserAuthController {

    @Autowired
    private JwtUtil jwtUtil;

    private final UserService userService;

    public UserAuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user with username, email, fullname, and password. This endpoint does not require authentication.",
            security = {},
            responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Username or email already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HTTPError.class))
            )
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request, HttpServletRequest req,
            HttpServletResponse response) {
        if (userService.isUsernameOrEmailTaken(request.getUsername(), request.getEmail())) {
            HTTPError httpError = new HTTPError(req, response);
            httpError.setMessage("Username or email already exists");
            httpError.setStatus(HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.badRequest().body(httpError);
        }

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("USER"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullname(request.getFullname());
        user.setAuthorities(authorities);

        User userResp = userService.registerUser(user, request.getPassword());
        ApiResponse apiResponse = new ApiResponse(req, userResp);
        apiResponse.setMessage("User registered successfully");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/signin")
    @Operation(
            summary = "Sign in a user",
            description = "Authenticates a user using their username or email and password. Returns a JWT token upon successful authentication which can be used further to access other endpoints. This endpoint does not require authentication.",
            security = {},
            responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Sign-in successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid username or password",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HTTPError.class))
            )
    })
    public ResponseEntity<?> signIn(@Valid @RequestBody SigninRequest signInRequest, HttpServletRequest request,
            HttpServletResponse response) {
        String usernameOrEmail = signInRequest.getUsername();
        String password = signInRequest.getPassword();

        boolean isEmail = EmailUtils.isEmail(usernameOrEmail);
        Optional<User> authenticatedUser;

        if (isEmail) {
            authenticatedUser = userService.authenticateByEmail(usernameOrEmail, password);
        } else {
            authenticatedUser = userService.authenticateByUsername(usernameOrEmail, password);
        }

        if (authenticatedUser.isPresent()) {

            String token = jwtUtil.generateToken(usernameOrEmail);
            Map<String, Object> responsePayload = new HashMap<>();
            responsePayload.put("user", authenticatedUser); // User data
            responsePayload.put("token", token);

            ApiResponse apiResponse = new ApiResponse(request, responsePayload);
            apiResponse.setMessage("Sign-in successful");
            apiResponse.setData(responsePayload);

            return ResponseEntity.ok(apiResponse);
        } else {
            HTTPError httpError = new HTTPError(request, response);

            httpError.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpError.setMessage("Invalid username or password");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(httpError);
        }
    }
}
