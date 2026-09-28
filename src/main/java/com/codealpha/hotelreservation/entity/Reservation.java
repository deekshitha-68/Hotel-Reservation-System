package com.codealpha.hotelreservation.entity;

import com.codealpha.hotelreservation.entity.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA entity representing a hotel reservation (booking).
 *
 * <p>A reservation links a {@link Customer} to a {@link Room} for a specific
 * date range. The lifecycle follows: PENDING → CONFIRMED (after payment) or
 * PENDING → CANCELLED.
 *
 * <p><strong>Availability Logic:</strong> Only reservations with status
 * {@code CONFIRMED} block room availability for their date range. When checking
 * if a room is available, the system queries for overlapping CONFIRMED
 * reservations. Two date ranges [checkIn1, checkOut1) and [checkIn2, checkOut2)
 * overlap when: {@code checkIn1 < checkOut2 AND checkIn2 < checkOut1}.
 *
 * <p><strong>Price Calculation:</strong> {@code numberOfNights} and
 * {@code totalAmount} are computed server-side in {@code ReservationService}
 * to ensure they can't be spoofed by the client:
 * <pre>
 *   numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut)
 *   totalAmount    = room.pricePerNight × numberOfNights
 * </pre>
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    /** The customer who made this reservation. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /** The room being reserved. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /** First night of the stay (inclusive). */
    @Column(nullable = false)
    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;

    /** Day of departure (exclusive — guest checks out this morning). */
    @Column(nullable = false)
    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;

    /** Number of guests staying in the room. */
    @Column(nullable = false)
    @Min(value = 1, message = "Number of guests must be at least 1")
    private int numberOfGuests;

    /**
     * Computed field: number of nights = checkOut - checkIn.
     * Calculated in ReservationService, not by the client.
     */
    @Column(nullable = false)
    private int numberOfNights;

    /**
     * Computed field: total cost = pricePerNight × numberOfNights.
     * Calculated in ReservationService, not by the client.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /** Current lifecycle status of this reservation. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    /** Timestamp when this reservation was first created. Set automatically. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime bookingDate;

    // =========================================================================
    // Lifecycle callback — auto-set bookingDate on creation
    // =========================================================================

    /**
     * JPA lifecycle callback: sets the booking timestamp to "now"
     * just before the entity is first persisted to the database.
     */
    @PrePersist
    protected void onCreate() {
        this.bookingDate = LocalDateTime.now();
    }

    // =========================================================================
    // Constructors
    // =========================================================================

    /** Default no-arg constructor required by JPA. */
    public Reservation() {
    }

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(int numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }
}
