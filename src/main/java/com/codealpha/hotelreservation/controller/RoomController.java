package com.codealpha.hotelreservation.controller;

import com.codealpha.hotelreservation.dto.RoomResponse;
import com.codealpha.hotelreservation.entity.enums.RoomType;
import com.codealpha.hotelreservation.service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for public-facing room endpoints.
 *
 * <p>Provides endpoints to list active rooms, search available rooms
 * by date/capacity/type, and view individual room details.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * GET /api/rooms — List all active rooms.
     *
     * @return 200 OK with list of active rooms
     */
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllActiveRooms() {
        return ResponseEntity.ok(roomService.getAllActiveRooms());
    }

    /**
     * GET /api/rooms/search — Search available rooms by dates, guests, and type.
     *
     * <p>All parameters are required except {@code roomType}, which is optional.
     * Example: {@code /api/rooms/search?checkIn=2024-12-20&checkOut=2024-12-25&guests=2&roomType=DELUXE}
     *
     * @param checkIn  requested check-in date (ISO format: yyyy-MM-dd)
     * @param checkOut requested check-out date (ISO format: yyyy-MM-dd)
     * @param guests   minimum guest count the room must accommodate
     * @param roomType optional room category filter (STANDARD, DELUXE, SUITE)
     * @return 200 OK with list of available rooms matching the criteria
     */
    @GetMapping("/search")
    public ResponseEntity<List<RoomResponse>> searchAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam int guests,
            @RequestParam(required = false) RoomType roomType) {

        List<RoomResponse> rooms = roomService.searchAvailableRooms(
                checkIn, checkOut, guests, roomType);
        return ResponseEntity.ok(rooms);
    }

    /**
     * GET /api/rooms/{id} — Get details for a single room.
     *
     * @param id the room's primary key
     * @return 200 OK with room details, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }
}
