package com.imshawan.rest.dto;

import jakarta.validation.constraints.*;

public class SigninRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 4, message = "Password should be at least 4 characters")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String usernameOrEmail) {
        this.username = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
