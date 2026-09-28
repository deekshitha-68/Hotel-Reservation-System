package com.codealpha.hotelreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Hotel Reservation System.
 *
 * <p>{@code @SpringBootApplication} combines three annotations:
 * <ul>
 *   <li>{@code @Configuration} — marks this class as a source of bean definitions</li>
 *   <li>{@code @EnableAutoConfiguration} — tells Spring Boot to auto-configure beans
 *       based on the dependencies on the classpath (e.g., JPA, Web, Validation)</li>
 *   <li>{@code @ComponentScan} — scans this package and sub-packages for Spring
 *       components (@Controller, @Service, @Repository, etc.)</li>
 * </ul>
 *
 * <p>The static frontend (HTML/CSS/JS) is served automatically from
 * {@code src/main/resources/static/}, so no separate web server is needed.
 */
@SpringBootApplication
public class HotelReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelReservationApplication.class, args);
    }
}
