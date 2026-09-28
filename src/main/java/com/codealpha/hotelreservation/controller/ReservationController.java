package com.codealpha.hotelreservation.controller;

import com.codealpha.hotelreservation.dto.ReservationRequest;
import com.codealpha.hotelreservation.dto.ReservationResponse;
import com.codealpha.hotelreservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for reservation (booking) endpoints.
 *
 * <p>Provides endpoints to create reservations, view booking details,
 * look up bookings by customer email, and cancel reservations.
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * POST /api/reservations — Create a new reservation.
     *
     * <p>Validates the request body, checks room availability, computes
     * price server-side, and creates a PENDING reservation.
     *
     * @param request the reservation request DTO (validated with @Valid)
     * @return 201 Created with the reservation details
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {
        ReservationResponse reservation = reservationService.createReservation(request);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    /**
     * GET /api/reservations/{id} — Get details for a single reservation.
     *
     * @param id the reservation's primary key
     * @return 200 OK with reservation details, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    /**
     * GET /api/reservations?email=... — List a customer's reservations.
     *
     * <p>Used for the "My Bookings" feature. Returns all reservations
     * associated with the given email address, ordered by most recent first.
     *
     * @param email the customer's email address
     * @return 200 OK with list of reservations (may be empty)
     */
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservationsByEmail(
            @RequestParam String email) {
        return ResponseEntity.ok(reservationService.getReservationsByEmail(email));
    }

    /**
     * PUT /api/reservations/{id}/cancel — Cancel a reservation.
     *
     * <p>Sets the reservation status to CANCELLED. The room becomes
     * available again for the previously blocked date range.
     *
     * @param id the reservation to cancel
     * @return 200 OK with updated reservation details
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }
}
