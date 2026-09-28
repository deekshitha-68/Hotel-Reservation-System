package com.codealpha.hotelreservation.entity.enums;

/**
 * Supported payment methods for simulated hotel payments.
 *
 * <ul>
 *   <li>UPI — Unified Payments Interface (Indian digital payment)</li>
 *   <li>CARD — Credit or debit card</li>
 *   <li>CASH — Cash payment at the hotel</li>
 * </ul>
 *
 * <p>Note: All payments in this system are simulated — no real payment
 * gateway is integrated. The payment method is recorded for demonstration purposes.
 */
public enum PaymentMethod {
    UPI,
    CARD,
    CASH
}
