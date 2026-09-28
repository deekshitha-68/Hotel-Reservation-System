package com.codealpha.hotelreservation.entity;

import com.codealpha.hotelreservation.entity.enums.PaymentMethod;
import com.codealpha.hotelreservation.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity representing a payment transaction for a reservation.
 *
 * <p>Each reservation has at most one payment (OneToOne relationship).
 * Payments in this system are simulated — no real payment gateway is
 * integrated. The payment method and status are recorded for demo purposes.
 *
 * <p>When a payment is processed successfully (status = SUCCESS), the
 * associated reservation's status is also updated to CONFIRMED.
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    /** The reservation this payment is for. One payment per reservation. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    /** Amount paid — should match the reservation's totalAmount. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /** Method of payment chosen by the customer. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    /** Current status of this payment transaction. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    /** Timestamp when the payment was processed. */
    @Column(nullable = false)
    private LocalDateTime paymentDate;

    // =========================================================================
    // Lifecycle callback
    // =========================================================================

    /** Auto-set the payment timestamp before first persist. */
    @PrePersist
    protected void onCreate() {
        this.paymentDate = LocalDateTime.now();
    }

    // =========================================================================
    // Constructors
    // =========================================================================

    /** Default no-arg constructor required by JPA. */
    public Payment() {
    }

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}
