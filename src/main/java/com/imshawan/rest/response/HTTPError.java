package com.imshawan.rest.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Shawan Mandal <github@imshawan.dev>
 * 
 * @implNote HTTPError is a class that encapsulates error details for HTTP responses,
 * including the error message, status code, status name, and the request URI.
 * It is designed to standardize error responses in a JSON format for API communication.
 */
public class HTTPError {
    private String message; 
    private int status; 
    private String path;     // Path of the path that caused the error
    private String statusMessage; // The elaborated detail of the status code (e.g., "Bad Request")
    

    public HTTPError(HttpServletRequest request, HttpServletResponse response) {
        this.path = request.getRequestURI(); // Get the path from the request
        this.status = response.getStatus();  // Get the response status code
        this.statusMessage = HttpStatus.valueOf(this.status).getReasonPhrase(); // Get the status name (e.g., "Bad Request")
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        this.statusMessage = HttpStatus.valueOf(status).getReasonPhrase();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getstatusMessage() {
        return statusMessage;
    }

    public void setstatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * @implNote Converts the current HTTPError object into its JSON representation.
     *
     * @return A JSON string representing the HTTPError.
     */
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "{\"error\": \"Failed to parse error to JSON\"}";
        }
    }
}
