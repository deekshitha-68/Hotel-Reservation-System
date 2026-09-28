package com.codealpha.hotelreservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * JPA entity representing a hotel customer / guest.
 *
 * <p>Customer records are created or looked up (by email) when a reservation
 * is made. If a customer with the same email already exists, the existing
 * record is reused so that "My Bookings" can aggregate all their reservations.
 *
 * <p>Relationship: One Customer can have many Reservations.
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    /** Full name of the customer. */
    @Column(nullable = false)
    @NotBlank(message = "Customer name is required")
    private String name;

    /**
     * Email address — used as the primary customer identifier for lookups.
     * Validated with {@code @Email} to ensure proper format.
     */
    @Column(nullable = false)
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    /**
     * Phone number — validated to accept common formats (digits, spaces,
     * dashes, parentheses, optional leading +). Must be 7-15 characters.
     */
    @Column(nullable = false)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,15}$",
             message = "Please provide a valid phone number")
    private String phone;

    // =========================================================================
    // Constructors
    // =========================================================================

    /** Default no-arg constructor required by JPA. */
    public Customer() {
    }

    public Customer(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
