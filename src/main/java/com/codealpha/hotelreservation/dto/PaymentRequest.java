package com.codealpha.hotelreservation.dto;

import com.codealpha.hotelreservation.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for incoming payment processing requests.
 *
 * <p>Contains the reservation ID to pay for and the chosen payment method.
 * The payment amount is derived from the reservation's totalAmount on the
 * server side — the client does not specify it.
 */
public class PaymentRequest {

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
