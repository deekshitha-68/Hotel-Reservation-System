package com.codealpha.hotelreservation.entity.enums;

/**
 * Lifecycle status of a hotel reservation.
 *
 * <ul>
 *   <li>PENDING — reservation created but payment not yet completed</li>
 *   <li>CONFIRMED — payment successful, reservation is active</li>
 *   <li>CANCELLED — reservation cancelled by the customer</li>
 * </ul>
 *
 * <p>Only CONFIRMED reservations block room availability for their date range.
 * PENDING and CANCELLED reservations do not prevent other bookings.
 */
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
