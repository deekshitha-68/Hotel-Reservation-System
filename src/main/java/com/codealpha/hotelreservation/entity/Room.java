package com.codealpha.hotelreservation.entity;

import com.codealpha.hotelreservation.entity.enums.RoomStatus;
import com.codealpha.hotelreservation.entity.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * JPA entity representing a hotel room.
 *
 * <p>Each room has a unique room number, a category (Standard/Deluxe/Suite),
 * a per-night price, a guest capacity, and an administrative status.
 *
 * <p>The {@code status} field (ACTIVE/INACTIVE) is for admin-level management
 * (e.g., taking a room offline for renovation). It does NOT indicate whether
 * the room is currently booked — booking availability is determined dynamically
 * by checking reservation date overlaps in {@code ReservationRepository}.
 *
 * <p>Relationship: One Room can have many Reservations over time.
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    /** Unique human-readable room identifier, e.g., "101", "205A". */
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    /** Category of the room — determines amenity level and typical price range. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Room type is required")
    private RoomType roomType;

    /** Nightly rate in the hotel's base currency. Always stored as BigDecimal for precision. */
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal pricePerNight;

    /** Maximum number of guests the room can accommodate. */
    @Column(nullable = false)
    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

    /** Detailed description of the room's features and amenities. */
    @Column(length = 1000)
    private String description;

    /**
     * Administrative status — ACTIVE rooms appear in search results,
     * INACTIVE rooms are hidden but their data is preserved.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Room status is required")
    private RoomStatus status = RoomStatus.ACTIVE;

    // =========================================================================
    // Constructors
    // =========================================================================

    /** Default no-arg constructor required by JPA. */
    public Room() {
    }

    /** Full constructor for programmatic room creation. */
    public Room(String roomNumber, RoomType roomType, BigDecimal pricePerNight,
                int capacity, String description, RoomStatus status) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.description = description;
        this.status = status;
    }

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
