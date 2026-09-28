package com.codealpha.hotelreservation.service;

import com.codealpha.hotelreservation.dto.PaymentRequest;
import com.codealpha.hotelreservation.dto.PaymentResponse;
import com.codealpha.hotelreservation.entity.Payment;
import com.codealpha.hotelreservation.entity.Reservation;
import com.codealpha.hotelreservation.entity.enums.BookingStatus;
import com.codealpha.hotelreservation.entity.enums.PaymentStatus;
import com.codealpha.hotelreservation.exception.ResourceNotFoundException;
import com.codealpha.hotelreservation.repository.PaymentRepository;
import com.codealpha.hotelreservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for payment processing.
 *
 * <p>Handles simulated payment transactions. When a payment is processed
 * successfully, both the Payment record (status = SUCCESS) and the
 * associated Reservation (status = CONFIRMED) are updated atomically
 * within a single transaction.
 *
 * <p><strong>Note:</strong> This is a simulated payment — no real payment
 * gateway is integrated. All payments are automatically marked as SUCCESS
 * for demonstration purposes.
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Process a simulated payment for a reservation.
     *
     * <p>Workflow:
     * <ol>
     *   <li>Look up the reservation and verify it exists</li>
     *   <li>Verify the reservation is in PENDING status</li>
     *   <li>Verify no payment already exists for this reservation</li>
     *   <li>Create a Payment record with SUCCESS status</li>
     *   <li>Update the reservation status to CONFIRMED</li>
     * </ol>
     *
     * @param request the payment request DTO (reservation ID + method)
     * @return the payment confirmation as a DTO
     * @throws ResourceNotFoundException if the reservation doesn't exist
     * @throws IllegalStateException     if reservation is not PENDING or already paid
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {

        // --- Look up the reservation ---
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with ID: " + request.getReservationId()));

        // --- Verify reservation is in PENDING status ---
        if (reservation.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot process payment: reservation is " +
                    reservation.getBookingStatus().name() +
                    " (must be PENDING)");
        }

        // --- Check for existing payment ---
        if (paymentRepository.findByReservationReservationId(
                reservation.getReservationId()).isPresent()) {
            throw new IllegalStateException(
                    "Payment already exists for reservation " +
                    reservation.getReservationId());
        }

        // --- Create payment record (simulated — always SUCCESS) ---
        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(reservation.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        Payment savedPayment = paymentRepository.save(payment);

        // --- Update reservation status to CONFIRMED ---
        reservation.setBookingStatus(BookingStatus.CONFIRMED);
        reservationRepository.save(reservation);

        return toPaymentResponse(savedPayment);
    }

    // =========================================================================
    // Entity → DTO conversion
    // =========================================================================

    /**
     * Converts a Payment entity to a PaymentResponse DTO.
     */
    private PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse dto = new PaymentResponse();
        dto.setPaymentId(payment.getPaymentId());
        dto.setReservationId(payment.getReservation().getReservationId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod().name());
        dto.setPaymentStatus(payment.getPaymentStatus().name());
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }
}
