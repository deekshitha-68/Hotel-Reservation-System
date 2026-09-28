package com.codealpha.hotelreservation.service;

import com.codealpha.hotelreservation.dto.ReservationRequest;
import com.codealpha.hotelreservation.dto.ReservationResponse;
import com.codealpha.hotelreservation.entity.Customer;
import com.codealpha.hotelreservation.entity.Payment;
import com.codealpha.hotelreservation.entity.Reservation;
import com.codealpha.hotelreservation.entity.Room;
import com.codealpha.hotelreservation.entity.enums.BookingStatus;
import com.codealpha.hotelreservation.entity.enums.RoomStatus;
import com.codealpha.hotelreservation.exception.ResourceNotFoundException;
import com.codealpha.hotelreservation.exception.RoomNotAvailableException;
import com.codealpha.hotelreservation.repository.CustomerRepository;
import com.codealpha.hotelreservation.repository.PaymentRepository;
import com.codealpha.hotelreservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for reservation (booking) operations.
 *
 * <p>This is the core business logic layer that handles:
 * <ul>
 *   <li>Creating reservations with full validation</li>
 *   <li>Server-side price calculation (source of truth)</li>
 *   <li>Room availability enforcement via date-overlap detection</li>
 *   <li>Booking cancellation</li>
 *   <li>Customer lookup by email ("My Bookings" feature)</li>
 * </ul>
 *
 * <p><strong>Key Design Decision:</strong> Price and number of nights are
 * always computed server-side, never trusted from the client. This prevents
 * price manipulation by malicious API consumers.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final RoomService roomService;

    public ReservationService(ReservationRepository reservationRepository,
                              CustomerRepository customerRepository,
                              PaymentRepository paymentRepository,
                              RoomService roomService) {
        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.roomService = roomService;
    }

    /**
     * Create a new reservation.
     *
     * <p>This method performs the full booking workflow:
     * <ol>
     *   <li>Validate date range (check-out must be after check-in)</li>
     *   <li>Look up the room and verify it's ACTIVE</li>
     *   <li>Verify guest count doesn't exceed room capacity</li>
     *   <li>Check room availability (no overlapping CONFIRMED reservations)</li>
     *   <li>Find or create the customer record by email</li>
     *   <li>Compute number of nights and total price server-side</li>
     *   <li>Save the reservation with PENDING status</li>
     * </ol>
     *
     * @param request the booking request DTO
     * @return the created reservation as a response DTO
     * @throws IllegalArgumentException   if dates are invalid or capacity exceeded
     * @throws ResourceNotFoundException  if the room doesn't exist
     * @throws RoomNotAvailableException  if the room is booked for those dates
     */
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {

        // --- Validate date range ---
        if (request.getCheckOut().isBefore(request.getCheckIn()) ||
                request.getCheckOut().isEqual(request.getCheckIn())) {
            throw new IllegalArgumentException(
                    "Check-out date must be after check-in date");
        }

        // --- Validate check-in is not in the past ---
        if (request.getCheckIn().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Check-in date cannot be in the past");
        }

        // --- Look up the room ---
        Room room = roomService.getRoomEntityById(request.getRoomId());

        // --- Verify room is active ---
        if (room.getStatus() != RoomStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Room " + room.getRoomNumber() + " is currently unavailable");
        }

        // --- Validate guest count vs. room capacity ---
        if (request.getNumberOfGuests() > room.getCapacity()) {
            throw new IllegalArgumentException(
                    "Number of guests (" + request.getNumberOfGuests() +
                    ") exceeds room capacity (" + room.getCapacity() + ")");
        }

        // --- Check room availability (the critical overlap check) ---
        List<Reservation> overlapping = reservationRepository
                .findOverlappingReservations(
                        room.getRoomId(),
                        request.getCheckIn(),
                        request.getCheckOut());
        if (!overlapping.isEmpty()) {
            throw new RoomNotAvailableException(
                    "Room " + room.getRoomNumber() +
                    " is not available for the selected dates");
        }

        // --- Find or create customer by email ---
        Customer customer = customerRepository.findByEmail(request.getCustomerEmail())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer(
                            request.getCustomerName(),
                            request.getCustomerEmail(),
                            request.getCustomerPhone());
                    return customerRepository.save(newCustomer);
                });

        // Update customer name and phone in case they've changed
        customer.setName(request.getCustomerName());
        customer.setPhone(request.getCustomerPhone());
        customerRepository.save(customer);

        // --- Compute number of nights and total price (SERVER-SIDE) ---
        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        BigDecimal totalAmount = room.getPricePerNight()
                .multiply(BigDecimal.valueOf(nights));

        // --- Build and save the reservation ---
        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setRoom(room);
        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckOut(request.getCheckOut());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setNumberOfNights((int) nights);
        reservation.setTotalAmount(totalAmount);
        reservation.setBookingStatus(BookingStatus.PENDING);

        Reservation saved = reservationRepository.save(reservation);
        return toReservationResponse(saved);
    }

    /**
     * Get a single reservation by its ID.
     *
     * @param reservationId the reservation's primary key
     * @return the reservation details as a DTO
     * @throws ResourceNotFoundException if no reservation exists with the given ID
     */
    public ReservationResponse getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with ID: " + reservationId));
        return toReservationResponse(reservation);
    }

    /**
     * Get all reservations for a customer identified by email.
     * Used for the "My Bookings" feature.
     *
     * @param email the customer's email address
     * @return list of reservations (most recent first)
     */
    public List<ReservationResponse> getReservationsByEmail(String email) {
        return reservationRepository.findByCustomerEmail(email).stream()
                .map(this::toReservationResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cancel a reservation.
     *
     * <p>Sets the booking status to CANCELLED. Once cancelled, the room
     * becomes available again for the previously blocked date range
     * (because the overlap query only considers CONFIRMED reservations).
     *
     * @param reservationId the reservation to cancel
     * @return the updated reservation as a DTO
     * @throws ResourceNotFoundException if the reservation doesn't exist
     * @throws IllegalStateException     if the reservation is already cancelled
     */
    @Transactional
    public ReservationResponse cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with ID: " + reservationId));

        if (reservation.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Reservation " + reservationId + " is already cancelled");
        }

        reservation.setBookingStatus(BookingStatus.CANCELLED);
        Reservation saved = reservationRepository.save(reservation);
        return toReservationResponse(saved);
    }

    // =========================================================================
    // Entity → DTO conversion
    // =========================================================================

    /**
     * Converts a Reservation entity (with its related Customer and Room)
     * into a flat ReservationResponse DTO.
     *
     * <p>Eagerly accesses lazy-loaded associations within the transaction
     * boundary to prevent LazyInitializationException.
     */
    private ReservationResponse toReservationResponse(Reservation reservation) {
        ReservationResponse dto = new ReservationResponse();

        // Reservation fields
        dto.setReservationId(reservation.getReservationId());
        dto.setCheckIn(reservation.getCheckIn());
        dto.setCheckOut(reservation.getCheckOut());
        dto.setNumberOfGuests(reservation.getNumberOfGuests());
        dto.setNumberOfNights(reservation.getNumberOfNights());
        dto.setTotalAmount(reservation.getTotalAmount());
        dto.setBookingStatus(reservation.getBookingStatus().name());
        dto.setBookingDate(reservation.getBookingDate());

        // Customer fields (flattened from the ManyToOne relationship)
        Customer customer = reservation.getCustomer();
        dto.setCustomerId(customer.getCustomerId());
        dto.setCustomerName(customer.getName());
        dto.setCustomerEmail(customer.getEmail());
        dto.setCustomerPhone(customer.getPhone());

        // Room fields (flattened from the ManyToOne relationship)
        Room room = reservation.getRoom();
        dto.setRoomId(room.getRoomId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomType(room.getRoomType().name());
        dto.setPricePerNight(room.getPricePerNight());

        // Payment fields (optional — may not exist yet for PENDING reservations)
        paymentRepository.findByReservationReservationId(reservation.getReservationId())
                .ifPresent(payment -> {
                    dto.setPaymentId(payment.getPaymentId());
                    dto.setPaymentMethod(payment.getPaymentMethod().name());
                    dto.setPaymentStatus(payment.getPaymentStatus().name());
                    dto.setPaymentDate(payment.getPaymentDate());
                });

        return dto;
    }
}
