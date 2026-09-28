package com.codealpha.hotelreservation.dto;

import java.time.LocalDateTime;

/**
 * Standard error response body returned by the global exception handler.
 *
 * <p>Provides a clean, non-technical JSON error message to API consumers.
 * The frontend reads the {@code message} field to display user-friendly
 * error alerts instead of raw stack traces.
 */
public class ErrorResponse {

    /** HTTP status code (e.g., 400, 404, 409). */
    private int status;

    /** Human-readable error description. */
    private String message;

    /** When the error occurred. */
    private LocalDateTime timestamp;

    // =========================================================================
    // Constructors
    // =========================================================================

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
