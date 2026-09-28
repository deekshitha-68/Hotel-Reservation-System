package com.codealpha.hotelreservation.controller;

import com.codealpha.hotelreservation.dto.RoomResponse;
import com.codealpha.hotelreservation.entity.Room;
import com.codealpha.hotelreservation.entity.enums.RoomStatus;
import com.codealpha.hotelreservation.entity.enums.RoomType;
import com.codealpha.hotelreservation.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for admin room management (optional / stretch goal).
 *
 * <p>Provides CRUD endpoints for managing rooms — adding new rooms,
 * updating existing rooms, and deactivating rooms. These are separated
 * from the public-facing {@link RoomController} under the {@code /api/admin}
 * path prefix.
 *
 * <p><strong>Note:</strong> In a production system, these endpoints would
 * be protected by authentication/authorization. For this demo, they are
 * open for simplicity.
 */
@RestController
@RequestMapping("/api/admin/rooms")
public class AdminRoomController {

    private final RoomService roomService;

    public AdminRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * GET /api/admin/rooms — List all rooms including inactive.
     */
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    /**
     * POST /api/admin/rooms — Add a new room.
     *
     * <p>Accepts a JSON body with room details and creates the room.
     */
    @PostMapping
    public ResponseEntity<RoomResponse> addRoom(@RequestBody Map<String, Object> body) {
        Room room = new Room();
        room.setRoomNumber((String) body.get("roomNumber"));
        room.setRoomType(RoomType.valueOf((String) body.get("roomType")));
        room.setPricePerNight(new BigDecimal(body.get("pricePerNight").toString()));
        room.setCapacity(Integer.parseInt(body.get("capacity").toString()));
        room.setDescription((String) body.get("description"));
        room.setStatus(RoomStatus.ACTIVE);

        RoomResponse saved = roomService.saveRoom(room);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * PUT /api/admin/rooms/{id} — Update an existing room.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long id, @RequestBody Map<String, Object> body) {
        Room room = roomService.getRoomEntityById(id);

        if (body.containsKey("roomNumber")) {
            room.setRoomNumber((String) body.get("roomNumber"));
        }
        if (body.containsKey("roomType")) {
            room.setRoomType(RoomType.valueOf((String) body.get("roomType")));
        }
        if (body.containsKey("pricePerNight")) {
            room.setPricePerNight(new BigDecimal(body.get("pricePerNight").toString()));
        }
        if (body.containsKey("capacity")) {
            room.setCapacity(Integer.parseInt(body.get("capacity").toString()));
        }
        if (body.containsKey("description")) {
            room.setDescription((String) body.get("description"));
        }
        if (body.containsKey("status")) {
            room.setStatus(RoomStatus.valueOf((String) body.get("status")));
        }

        return ResponseEntity.ok(roomService.saveRoom(room));
    }

    /**
     * DELETE /api/admin/rooms/{id} — Deactivate a room (soft delete).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateRoom(@PathVariable Long id) {
        roomService.deactivateRoom(id);
        return ResponseEntity.noContent().build();
    }
}
