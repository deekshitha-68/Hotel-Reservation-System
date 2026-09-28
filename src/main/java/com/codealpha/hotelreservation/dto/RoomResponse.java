package com.codealpha.hotelreservation.dto;

import java.math.BigDecimal;

/**
 * DTO for outgoing room data in API responses.
 *
 * <p>Provides a safe, flat representation of a Room entity.
 * Unlike the entity, this DTO contains no JPA annotations or lazy-loaded
 * associations, making it safe to serialize directly to JSON.
 */
public class RoomResponse {

    private Long roomId;
    private String roomNumber;
    private String roomType;
    private BigDecimal pricePerNight;
    private int capacity;
    private String description;
    private String status;

    // =========================================================================
    // Constructors
    // =========================================================================

    public RoomResponse() {
    }

    public RoomResponse(Long roomId, String roomNumber, String roomType,
                        BigDecimal pricePerNight, int capacity,
                        String description, String status) {
        this.roomId = roomId;
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

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
