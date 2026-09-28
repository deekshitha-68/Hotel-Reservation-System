package com.codealpha.hotelreservation.exception;

/**
 * Exception thrown when a requested resource (Room, Reservation, Customer)
 * is not found in the database.
 *
 * <p>Handled by {@link GlobalExceptionHandler} to return HTTP 404 with
 * a clean JSON error body.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
