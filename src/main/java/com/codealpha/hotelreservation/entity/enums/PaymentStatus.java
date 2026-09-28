package com.codealpha.hotelreservation.entity.enums;

/**
 * Status of a payment transaction.
 *
 * <ul>
 *   <li>PENDING — payment initiated but not yet processed</li>
 *   <li>SUCCESS — payment completed successfully</li>
 *   <li>FAILED — payment was declined or encountered an error</li>
 * </ul>
 */
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED
}
