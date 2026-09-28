package com.codealpha.hotelreservation.repository;

import com.codealpha.hotelreservation.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Payment} entities.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find the payment associated with a specific reservation.
     *
     * @param reservationId the reservation's ID
     * @return Optional containing the payment if found
     */
    Optional<Payment> findByReservationReservationId(Long reservationId);
}
