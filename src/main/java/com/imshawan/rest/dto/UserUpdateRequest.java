package com.imshawan.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @Size(min = 1, max = 20, message = "Full name must be between 1 and 20 characters")
    private String fullname;

    private String profilePicture;

    public UserUpdateRequest() {}

    public UserUpdateRequest(String fullname, String profilePicture) {
        this.profilePicture = "";
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
