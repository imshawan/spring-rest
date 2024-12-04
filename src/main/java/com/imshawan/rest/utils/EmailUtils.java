package com.imshawan.rest.utils;

public class EmailUtils {
    // Method to check if a given string is an email address
    public static boolean isEmail(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return content.matches(emailRegex);
    }
}
