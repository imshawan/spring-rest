package com.imshawan.rest.response;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Shawan Mandal <github@imshawan.dev>
 * 
 * @implNote SuccessResponse is a class that encapsulates the details of a successful HTTP response,
 * including the message, status code, status name, and the request URI.
 * It is designed to standardize success responses in a JSON format for API communication.
 */
public class ApiResponse {
    private String message;
    private int status;
    private String path;  // Path of the endpoint that generated the response
    private String statusMessage;  // The message for the status code (e.g., "OK")
    private Object data;  // Any data to be returned with the response

    public ApiResponse(HttpServletRequest request, Object data) {
        this.path = request.getRequestURI();  // Get the path from the request
        this.status = HttpStatus.OK.value();  // Default to HTTP 200 (OK)
        this.statusMessage = HttpStatus.valueOf(this.status).getReasonPhrase(); // Get the status message (e.g., "OK")
        this.data = data;
    }

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * @implNote Converts the current SuccessResponse object into its JSON representation.
     *
     * @return A JSON string representing the SuccessResponse.
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
