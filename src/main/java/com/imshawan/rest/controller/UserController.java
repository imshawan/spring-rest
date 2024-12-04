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
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request, HttpServletRequest req, HttpServletResponse response) {
        if (userService.isUsernameOrEmailTaken(request.getUsername(), request.getEmail())) {
            HTTPError httpError = new HTTPError(req, response);
            httpError.setMessage("Username or email already exists");
            httpError.setStatus(HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.badRequest().body(httpError);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullname(request.getFullname());

        User userResp = userService.registerUser(user, request.getPassword());
        ApiResponse apiResponse = new ApiResponse(req, userResp);
        apiResponse.setMessage("User registered successfully");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/signin")
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

        if (!authenticatedUser.isEmpty()) {

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

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserProfile(@PathVariable String id, @Valid @RequestBody User user) {
        Optional<User> updatedUser = userService.updateUserById(id, user);
        return updatedUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable String id) {
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
