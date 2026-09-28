package com.codealpha.hotelreservation.controller;

import com.codealpha.hotelreservation.dto.PaymentRequest;
import com.codealpha.hotelreservation.dto.PaymentResponse;
import com.codealpha.hotelreservation.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for payment endpoints.
 *
 * <p>Provides a single endpoint to simulate payment processing.
 * On successful payment, the associated reservation is automatically
 * confirmed.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * POST /api/payments — Process a simulated payment.
     *
     * <p>Accepts a reservation ID and payment method. Creates a payment
     * record with SUCCESS status and updates the reservation to CONFIRMED.
     *
     * @param request the payment request DTO (validated with @Valid)
     * @return 201 Created with payment confirmation details
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse payment = paymentService.processPayment(request);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }
}
