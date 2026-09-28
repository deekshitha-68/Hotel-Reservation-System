package com.codealpha.hotelreservation.repository;

import com.codealpha.hotelreservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Reservation} entities.
 *
 * <p>Contains the critical availability-overlap query that determines
 * whether a room is available for a requested date range.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Find all CONFIRMED reservations for a specific room that overlap with
     * the requested date range.
     *
     * <p><strong>Date Overlap Logic:</strong> Two half-open date ranges
     * [checkIn1, checkOut1) and [checkIn2, checkOut2) overlap when:
     * <pre>
     *   checkIn1 &lt; checkOut2  AND  checkIn2 &lt; checkOut1
     * </pre>
     *
     * <p>For example, if a room has a CONFIRMED reservation for Jan 5–8:
     * <ul>
     *   <li>Request Jan 3–6 → overlaps (Jan 5 &lt; Jan 6 AND Jan 3 &lt; Jan 8)</li>
     *   <li>Request Jan 7–10 → overlaps (Jan 5 &lt; Jan 10 AND Jan 7 &lt; Jan 8)</li>
     *   <li>Request Jan 8–10 → NO overlap (Jan 8 is NOT &lt; Jan 8) — this is OK
     *       because check-out is on the morning of Jan 8, and check-in for the
     *       new reservation is on the afternoon of Jan 8.</li>
     *   <li>Request Jan 1–5 → NO overlap (Jan 5 is NOT &lt; Jan 5)</li>
     * </ul>
     *
     * <p>Only CONFIRMED reservations are considered. PENDING and CANCELLED
     * reservations do NOT block availability.
     *
     * @param roomId   the room to check
     * @param checkIn  requested check-in date
     * @param checkOut requested check-out date
     * @return list of overlapping CONFIRMED reservations (empty = room is available)
     */
    @Query("SELECT r FROM Reservation r WHERE r.room.roomId = :roomId " +
           "AND r.bookingStatus = 'CONFIRMED' " +
           "AND r.checkIn < :checkOut AND r.checkOut > :checkIn")
    List<Reservation> findOverlappingReservations(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);

    /**
     * Find all reservations made by a customer with the given email.
     * Used for the "My Bookings" feature, ordered by most recent first.
     *
     * @param email the customer's email address
     * @return list of reservations for that customer
     */
    @Query("SELECT r FROM Reservation r WHERE r.customer.email = :email " +
           "ORDER BY r.bookingDate DESC")
    List<Reservation> findByCustomerEmail(@Param("email") String email);
}
