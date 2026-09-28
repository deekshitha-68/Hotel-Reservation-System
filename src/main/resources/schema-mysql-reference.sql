-- ============================================================================
-- Hotel Reservation System — Database Schema
-- ============================================================================
-- This file documents the database structure independently of JPA.
-- In practice, tables are auto-created by Hibernate (ddl-auto=update),
-- but this script serves as reference documentation.
--
-- Database: hotel_reservation_db
-- Engine: MySQL 8
-- ============================================================================

CREATE DATABASE IF NOT EXISTS hotel_reservation_db;
USE hotel_reservation_db;

-- ----------------------------
-- Table: rooms
-- ----------------------------
CREATE TABLE IF NOT EXISTS rooms (
    room_id       BIGINT       NOT NULL AUTO_INCREMENT,
    room_number   VARCHAR(255) NOT NULL UNIQUE,
    room_type     VARCHAR(20)  NOT NULL COMMENT 'STANDARD, DELUXE, or SUITE',
    price_per_night DECIMAL(10,2) NOT NULL,
    capacity      INT          NOT NULL,
    description   VARCHAR(1000),
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE or INACTIVE',
    PRIMARY KEY (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: customers
-- ----------------------------
CREATE TABLE IF NOT EXISTS customers (
    customer_id   BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone         VARCHAR(255) NOT NULL,
    PRIMARY KEY (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: reservations
-- ----------------------------
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id   BIGINT        NOT NULL AUTO_INCREMENT,
    customer_id      BIGINT        NOT NULL,
    room_id          BIGINT        NOT NULL,
    check_in         DATE          NOT NULL,
    check_out        DATE          NOT NULL,
    number_of_guests INT           NOT NULL,
    number_of_nights INT           NOT NULL,
    total_amount     DECIMAL(10,2) NOT NULL,
    booking_status   VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, CONFIRMED, or CANCELLED',
    booking_date     DATETIME      NOT NULL,
    PRIMARY KEY (reservation_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (room_id)     REFERENCES rooms(room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table: payments
-- ----------------------------
CREATE TABLE IF NOT EXISTS payments (
    payment_id     BIGINT        NOT NULL AUTO_INCREMENT,
    reservation_id BIGINT        NOT NULL UNIQUE,
    amount         DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(20)   NOT NULL COMMENT 'UPI, CARD, or CASH',
    payment_status VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, SUCCESS, or FAILED',
    payment_date   DATETIME      NOT NULL,
    PRIMARY KEY (payment_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
