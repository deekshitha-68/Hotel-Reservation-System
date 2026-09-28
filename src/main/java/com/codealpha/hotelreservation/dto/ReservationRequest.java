package com.codealpha.hotelreservation.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) for incoming reservation creation requests.
 *
 * <p>This DTO captures all the information needed to create a reservation:
 * room selection, customer details, dates, and guest count. It is validated
 * using Bean Validation annotations before the service layer processes it.
 *
 * <p>Using a DTO instead of the JPA entity directly avoids:
 * <ul>
 *   <li>Exposing internal entity structure to the API consumer</li>
 *   <li>Lazy-loading and circular-reference issues during JSON serialization</li>
 *   <li>Allowing the client to set server-computed fields (price, nights, status)</li>
 * </ul>
 */
public class ReservationRequest {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String customerEmail;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,15}$",
             message = "Please provide a valid phone number")
    private String customerPhone;

    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;

    @Min(value = 1, message = "Number of guests must be at least 1")
    private int numberOfGuests;

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
}
