package com.codealpha.hotelreservation.repository;

import com.codealpha.hotelreservation.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Customer} entities.
 *
 * <p>When a reservation is created, the service layer uses {@code findByEmail}
 * to check if the customer already exists. If so, the existing record is
 * reused; otherwise, a new Customer is created. This ensures all bookings
 * for the same email address are linked to a single customer record.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Look up a customer by their email address.
     *
     * @param email the customer's email
     * @return Optional containing the customer if found, empty otherwise
     */
    Optional<Customer> findByEmail(String email);
}
