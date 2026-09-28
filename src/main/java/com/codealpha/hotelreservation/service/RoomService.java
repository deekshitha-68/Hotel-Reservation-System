package com.codealpha.hotelreservation.service;

import com.codealpha.hotelreservation.dto.RoomResponse;
import com.codealpha.hotelreservation.entity.Room;
import com.codealpha.hotelreservation.entity.enums.RoomStatus;
import com.codealpha.hotelreservation.entity.enums.RoomType;
import com.codealpha.hotelreservation.exception.ResourceNotFoundException;
import com.codealpha.hotelreservation.repository.ReservationRepository;
import com.codealpha.hotelreservation.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for room-related operations.
 *
 * <p>Handles listing active rooms, searching with availability filtering,
 * and retrieving individual room details. Converts entities to DTOs
 * before returning to the controller layer.
 */
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomService(RoomRepository roomRepository,
                       ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * List all rooms with ACTIVE status.
     *
     * @return list of active rooms as DTOs
     */
    public List<RoomResponse> getAllActiveRooms() {
        return roomRepository.findByStatus(RoomStatus.ACTIVE).stream()
                .map(this::toRoomResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search for rooms available during the given date range, optionally
     * filtered by minimum capacity and room type.
     *
     * <p><strong>Availability Logic:</strong> For each active room, we query
     * the reservation table for any CONFIRMED reservations that overlap with
     * the requested date range. If no overlapping reservations exist, the
     * room is available. See {@link ReservationRepository#findOverlappingReservations}
     * for the overlap detection query.
     *
     * @param checkIn  requested check-in date
     * @param checkOut requested check-out date
     * @param guests   minimum number of guests the room must accommodate
     * @param roomType optional room type filter (null = any type)
     * @return list of available rooms matching the criteria
     */
    public List<RoomResponse> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut,
                                                    int guests, RoomType roomType) {
        // Step 1: Get all active rooms, optionally filtered by type
        List<Room> rooms;
        if (roomType != null) {
            rooms = roomRepository.findByStatusAndRoomType(RoomStatus.ACTIVE, roomType);
        } else {
            rooms = roomRepository.findByStatus(RoomStatus.ACTIVE);
        }

        // Step 2: Filter by capacity (room must accommodate the requested guest count)
        // Step 3: Filter by availability (no overlapping CONFIRMED reservations)
        return rooms.stream()
                .filter(room -> room.getCapacity() >= guests)
                .filter(room -> reservationRepository
                        .findOverlappingReservations(room.getRoomId(), checkIn, checkOut)
                        .isEmpty())
                .map(this::toRoomResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get details for a single room by its ID.
     *
     * @param roomId the room's primary key
     * @return the room details as a DTO
     * @throws ResourceNotFoundException if no room exists with the given ID
     */
    public RoomResponse getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + roomId));
        return toRoomResponse(room);
    }

    /**
     * Get the raw Room entity by ID (for internal service-layer use).
     * Not exposed to controllers — used by ReservationService.
     */
    public Room getRoomEntityById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + roomId));
    }

    /**
     * Get all rooms including inactive (for admin panel).
     */
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::toRoomResponse)
                .collect(Collectors.toList());
    }

    /**
     * Save a room entity (for admin operations).
     */
    public RoomResponse saveRoom(Room room) {
        Room saved = roomRepository.save(room);
        return toRoomResponse(saved);
    }

    /**
     * Delete (deactivate) a room by setting its status to INACTIVE.
     */
    public void deactivateRoom(Long roomId) {
        Room room = getRoomEntityById(roomId);
        room.setStatus(RoomStatus.INACTIVE);
        roomRepository.save(room);
    }

    // =========================================================================
    // Entity → DTO conversion
    // =========================================================================

    /**
     * Converts a Room JPA entity to a RoomResponse DTO.
     * This mapping ensures JPA internals (proxy objects, lazy collections)
     * are not leaked to the JSON response.
     */
    private RoomResponse toRoomResponse(Room room) {
        RoomResponse dto = new RoomResponse();
        dto.setRoomId(room.getRoomId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomType(room.getRoomType().name());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setCapacity(room.getCapacity());
        dto.setDescription(room.getDescription());
        dto.setStatus(room.getStatus().name());
        return dto;
    }
}
