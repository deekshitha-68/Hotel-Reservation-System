package com.codealpha.hotelreservation.exception;

/**
 * Exception thrown when a room booking request conflicts with an existing
 * CONFIRMED reservation (date-range overlap).
 *
 * <p>Handled by {@link GlobalExceptionHandler} to return HTTP 409 Conflict
 * with a descriptive error message.
 */
public class RoomNotAvailableException extends RuntimeException {

    public RoomNotAvailableException(String message) {
        super(message);
    }
}
