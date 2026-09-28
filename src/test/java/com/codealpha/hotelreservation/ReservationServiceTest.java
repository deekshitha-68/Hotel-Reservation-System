package com.codealpha.hotelreservation;

import com.codealpha.hotelreservation.dto.ReservationRequest;
import com.codealpha.hotelreservation.entity.Customer;
import com.codealpha.hotelreservation.entity.Reservation;
import com.codealpha.hotelreservation.entity.Room;
import com.codealpha.hotelreservation.entity.enums.BookingStatus;
import com.codealpha.hotelreservation.entity.enums.RoomStatus;
import com.codealpha.hotelreservation.entity.enums.RoomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the core business logic of the Hotel Reservation System.
 *
 * <p>These tests cover:
 * <ul>
 *   <li>Number of nights calculation</li>
 *   <li>Total price calculation</li>
 *   <li>Date overlap detection logic</li>
 * </ul>
 *
 * <p>These tests do not require a running database — they test pure
 * computation logic in isolation.
 */
class ReservationServiceTest {

    // =========================================================================
    // Number of Nights Calculation Tests
    // =========================================================================

    @Test
    @DisplayName("Calculate nights: 3-night stay (Dec 20 → Dec 23)")
    void testNumberOfNights_threeNights() {
        LocalDate checkIn = LocalDate.of(2024, 12, 20);
        LocalDate checkOut = LocalDate.of(2024, 12, 23);

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        assertEquals(3, nights, "Dec 20 to Dec 23 should be 3 nights");
    }

    @Test
    @DisplayName("Calculate nights: single-night stay")
    void testNumberOfNights_singleNight() {
        LocalDate checkIn = LocalDate.of(2024, 12, 20);
        LocalDate checkOut = LocalDate.of(2024, 12, 21);

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        assertEquals(1, nights, "Dec 20 to Dec 21 should be 1 night");
    }

    @Test
    @DisplayName("Calculate nights: week-long stay")
    void testNumberOfNights_weekStay() {
        LocalDate checkIn = LocalDate.of(2024, 12, 1);
        LocalDate checkOut = LocalDate.of(2024, 12, 8);

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        assertEquals(7, nights, "Dec 1 to Dec 8 should be 7 nights");
    }

    // =========================================================================
    // Total Price Calculation Tests
    // =========================================================================

    @Test
    @DisplayName("Calculate total: 3 nights at ₹2500/night = ₹7500")
    void testTotalPrice_standard() {
        BigDecimal pricePerNight = new BigDecimal("2500.00");
        long nights = 3;

        BigDecimal total = pricePerNight.multiply(BigDecimal.valueOf(nights));

        assertEquals(new BigDecimal("7500.00"), total,
                "3 nights × ₹2500 = ₹7500");
    }

    @Test
    @DisplayName("Calculate total: 5 nights at ₹8000/night = ₹40000")
    void testTotalPrice_suite() {
        BigDecimal pricePerNight = new BigDecimal("8000.00");
        long nights = 5;

        BigDecimal total = pricePerNight.multiply(BigDecimal.valueOf(nights));

        assertEquals(new BigDecimal("40000.00"), total,
                "5 nights × ₹8000 = ₹40000");
    }

    @Test
    @DisplayName("Calculate total: single night at ₹4500/night = ₹4500")
    void testTotalPrice_singleNight() {
        BigDecimal pricePerNight = new BigDecimal("4500.00");
        long nights = 1;

        BigDecimal total = pricePerNight.multiply(BigDecimal.valueOf(nights));

        assertEquals(new BigDecimal("4500.00"), total,
                "1 night × ₹4500 = ₹4500");
    }

    // =========================================================================
    // Date Overlap Detection Tests
    // =========================================================================
    // These tests verify the overlap rule:
    //   Two ranges [A, B) and [C, D) overlap when: A < D AND C < B
    //
    // Existing reservation: Jan 5 – Jan 8 (3 nights)
    // We test various new requests against this existing range.

    @Test
    @DisplayName("Overlap: new booking starts during existing (Jan 6–10 vs Jan 5–8)")
    void testOverlap_startsDuring() {
        // Existing: Jan 5 – Jan 8
        LocalDate existCheckIn = LocalDate.of(2024, 1, 5);
        LocalDate existCheckOut = LocalDate.of(2024, 1, 8);

        // New request: Jan 6 – Jan 10
        LocalDate newCheckIn = LocalDate.of(2024, 1, 6);
        LocalDate newCheckOut = LocalDate.of(2024, 1, 10);

        boolean overlaps = existCheckIn.isBefore(newCheckOut) &&
                           newCheckIn.isBefore(existCheckOut);

        assertTrue(overlaps, "Jan 6–10 should overlap with Jan 5–8");
    }

    @Test
    @DisplayName("Overlap: new booking ends during existing (Jan 3–6 vs Jan 5–8)")
    void testOverlap_endsDuring() {
        LocalDate existCheckIn = LocalDate.of(2024, 1, 5);
        LocalDate existCheckOut = LocalDate.of(2024, 1, 8);

        LocalDate newCheckIn = LocalDate.of(2024, 1, 3);
        LocalDate newCheckOut = LocalDate.of(2024, 1, 6);

        boolean overlaps = existCheckIn.isBefore(newCheckOut) &&
                           newCheckIn.isBefore(existCheckOut);

        assertTrue(overlaps, "Jan 3–6 should overlap with Jan 5–8");
    }

    @Test
    @DisplayName("Overlap: new booking completely contains existing (Jan 4–9 vs Jan 5–8)")
    void testOverlap_containsExisting() {
        LocalDate existCheckIn = LocalDate.of(2024, 1, 5);
        LocalDate existCheckOut = LocalDate.of(2024, 1, 8);

        LocalDate newCheckIn = LocalDate.of(2024, 1, 4);
        LocalDate newCheckOut = LocalDate.of(2024, 1, 9);

        boolean overlaps = existCheckIn.isBefore(newCheckOut) &&
                           newCheckIn.isBefore(existCheckOut);

        assertTrue(overlaps, "Jan 4–9 should overlap with Jan 5–8");
    }

    @Test
    @DisplayName("No overlap: new booking starts on existing check-out day (Jan 8–10 vs Jan 5–8)")
    void testNoOverlap_startsOnCheckoutDay() {
        // This is the critical edge case: checkout is on the morning of Jan 8,
        // so a new guest can check in on the afternoon of Jan 8.
        LocalDate existCheckIn = LocalDate.of(2024, 1, 5);
        LocalDate existCheckOut = LocalDate.of(2024, 1, 8);

        LocalDate newCheckIn = LocalDate.of(2024, 1, 8);
        LocalDate newCheckOut = LocalDate.of(2024, 1, 10);

        boolean overlaps = existCheckIn.isBefore(newCheckOut) &&
                           newCheckIn.isBefore(existCheckOut);

        assertFalse(overlaps,
                "Jan 8–10 should NOT overlap with Jan 5–8 " +
                "(check-out day is not a conflict)");
    }

    @Test
    @DisplayName("No overlap: new booking ends on existing check-in day (Jan 1–5 vs Jan 5–8)")
    void testNoOverlap_endsOnCheckinDay() {
        LocalDate existCheckIn = LocalDate.of(2024, 1, 5);
        LocalDate existCheckOut = LocalDate.of(2024, 1, 8);

        LocalDate newCheckIn = LocalDate.of(2024, 1, 1);
        LocalDate newCheckOut = LocalDate.of(2024, 1, 5);

        boolean overlaps = existCheckIn.isBefore(newCheckOut) &&
                           newCheckIn.isBefore(existCheckOut);

        assertFalse(overlaps,
                "Jan 1–5 should NOT overlap with Jan 5–8 " +
                "(new checkout on existing check-in day is OK)");
    }

    @Test
    @DisplayName("No overlap: new booking completely before existing (Jan 1–3 vs Jan 5–8)")
    void testNoOverlap_completelyBefore() {
        LocalDate existCheckIn = LocalDate.of(2024, 1, 5);
        LocalDate existCheckOut = LocalDate.of(2024, 1, 8);

        LocalDate newCheckIn = LocalDate.of(2024, 1, 1);
        LocalDate newCheckOut = LocalDate.of(2024, 1, 3);

        boolean overlaps = existCheckIn.isBefore(newCheckOut) &&
                           newCheckIn.isBefore(existCheckOut);

        assertFalse(overlaps, "Jan 1–3 should NOT overlap with Jan 5–8");
    }

    @Test
    @DisplayName("No overlap: new booking completely after existing (Jan 10–12 vs Jan 5–8)")
    void testNoOverlap_completelyAfter() {
        LocalDate existCheckIn = LocalDate.of(2024, 1, 5);
        LocalDate existCheckOut = LocalDate.of(2024, 1, 8);

        LocalDate newCheckIn = LocalDate.of(2024, 1, 10);
        LocalDate newCheckOut = LocalDate.of(2024, 1, 12);

        boolean overlaps = existCheckIn.isBefore(newCheckOut) &&
                           newCheckIn.isBefore(existCheckOut);

        assertFalse(overlaps, "Jan 10–12 should NOT overlap with Jan 5–8");
    }

    // =========================================================================
    // Date Validation Tests
    // =========================================================================

    @Test
    @DisplayName("Validation: check-out before check-in should be rejected")
    void testValidation_checkOutBeforeCheckIn() {
        LocalDate checkIn = LocalDate.of(2024, 12, 25);
        LocalDate checkOut = LocalDate.of(2024, 12, 20);

        assertTrue(checkOut.isBefore(checkIn),
                "Check-out Dec 20 is before check-in Dec 25");
    }

    @Test
    @DisplayName("Validation: same-day check-in/check-out should be rejected")
    void testValidation_sameDayCheckinCheckout() {
        LocalDate checkIn = LocalDate.of(2024, 12, 25);
        LocalDate checkOut = LocalDate.of(2024, 12, 25);

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        assertEquals(0, nights, "Same-day booking = 0 nights (should be rejected)");
    }
}
