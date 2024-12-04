package com.imshawan.rest.exception;

import com.imshawan.rest.response.HTTPError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Shawan Mandal <github@imshawan.dev>
 * 
 * @implNote This class handles various exceptions globally within the
 *           application. It defines specific
 *           exception handlers for different error scenarios.
 * 
 *           All responses are formatted using a consistent `HTTPError`
 *           format, ensuring a unified error
 *           handling structure across the API.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles IllegalArgumentException. Returns a BAD_REQUEST (400) response.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HTTPError> handleIllegalArgumentException(IllegalArgumentException ex,
            HttpServletRequest request, HttpServletResponse response) {
        HTTPError httpError = new HTTPError(request, response);
        httpError.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpError);
    }

    /**
     * Handles validation errors (e.g., @Valid annotation). Returns a BAD_REQUEST
     * (400) response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HTTPError> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        HTTPError httpError = new HTTPError(request, response);
        httpError.setMessage(errorMessage);
        httpError.setStatus(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(httpError);
    }

    /**
     * Handles unsupported HTTP methods, e.g., PUT on endpoints that don't support
     * it.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HTTPError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request, HttpServletResponse response) {
        String errorMessage = "The requested method " + ex.getMethod() + " is not supported for this endpoint.";
        HTTPError httpError = new HTTPError(request, response);
        httpError.setMessage(errorMessage);
        httpError.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(httpError);
    }
}
