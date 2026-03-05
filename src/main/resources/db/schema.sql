-- =============================================================
-- Ocean View Resort - Database Schema
-- This file creates the entire database structure for the
-- Ocean View Resort room reservation system. It includes
-- tables, stored procedures, triggers, and default data.
-- =============================================================

-- Create the database if it doesn't already exist, then switch to it
CREATE DATABASE IF NOT EXISTS oceanview_resort;
USE oceanview_resort;

-- =============================================================
-- TABLE: users
-- This table stores all the user accounts (staff and admin)
-- who can log in to the reservation system.
-- =============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,   -- user_id: unique ID, auto-incremented by MySQL
    username      VARCHAR(50) NOT NULL UNIQUE,       -- username: login name, must be unique
    password_hash VARCHAR(255) NOT NULL,             -- password_hash: stores the bcrypt-hashed password (not plain text)
    full_name     VARCHAR(100) NOT NULL,             -- full_name: the user's display name
    email         VARCHAR(100),                      -- email: optional email address for the user
    role          ENUM('STAFF','ADMIN') NOT NULL DEFAULT 'STAFF', -- role: either STAFF (limited access) or ADMIN (full access)
    is_active     BOOLEAN DEFAULT TRUE,              -- is_active: if FALSE, the user cannot log in
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,           -- created_at: when this account was created
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- updated_at: auto-updates whenever the row changes
) ENGINE=InnoDB;

-- =============================================================
-- TABLE: rooms
-- This table stores all the hotel rooms and their details.
-- Each room has a type (STANDARD, DELUXE, or SUITE) and a
-- nightly price in LKR (Sri Lankan Rupees).
-- =============================================================
CREATE TABLE IF NOT EXISTS rooms (
    room_id         INT AUTO_INCREMENT PRIMARY KEY,  -- room_id: unique ID, auto-incremented by MySQL
    room_number     VARCHAR(10) NOT NULL UNIQUE,     -- room_number: the physical room number (e.g., '101', '302')
    room_type       ENUM('STANDARD','DELUXE','SUITE') NOT NULL, -- room_type: category of the room
    price_per_night DECIMAL(10,2) NOT NULL,          -- price_per_night: cost per night in LKR
    is_available    BOOLEAN DEFAULT TRUE,            -- is_available: FALSE means the room is out of service
    description     TEXT,                            -- description: a short text describing the room's features
    max_occupancy   INT DEFAULT 2,                   -- max_occupancy: maximum number of guests allowed
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- created_at: when this room record was added
) ENGINE=InnoDB;

-- =============================================================
-- TABLE: reservations
-- This table stores all guest reservations (bookings).
-- Each reservation is linked to a room and optionally to the
-- staff member who created it.
-- =============================================================
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id     INT AUTO_INCREMENT PRIMARY KEY,    -- reservation_id: unique ID, auto-incremented by MySQL
    reservation_number VARCHAR(20) NOT NULL UNIQUE,       -- reservation_number: human-readable booking number (e.g., RES-100001)
    guest_name         VARCHAR(100) NOT NULL,             -- guest_name: full name of the guest
    guest_address      VARCHAR(255),                      -- guest_address: optional mailing address
    contact_number     VARCHAR(20) NOT NULL,              -- contact_number: guest's phone number (required)
    guest_email        VARCHAR(100),                      -- guest_email: optional email for the guest
    room_id            INT NOT NULL,                      -- room_id: which room is booked (links to rooms table)
    room_type          ENUM('STANDARD','DELUXE','SUITE') NOT NULL, -- room_type: stored here too for quick access
    check_in_date      DATE NOT NULL,                     -- check_in_date: when the guest arrives
    check_out_date     DATE NOT NULL,                     -- check_out_date: when the guest leaves
    status             ENUM('CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') NOT NULL DEFAULT 'CONFIRMED', -- status: tracks the reservation lifecycle
    special_requests   TEXT,                              -- special_requests: any notes from the guest (e.g., extra pillows)
    created_by         INT,                               -- created_by: user_id of the staff member who made this booking
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,           -- created_at: when the reservation was created
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- updated_at: auto-updates on any change
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),      -- This links each reservation to a room in the rooms table
    FOREIGN KEY (created_by) REFERENCES users(user_id),   -- This links the reservation to the staff member who created it
    CONSTRAINT chk_dates CHECK (check_out_date > check_in_date) -- This ensures checkout date is always after checkin date
) ENGINE=InnoDB;

-- =============================================================
-- TABLE: bills
-- This table stores the billing/invoice records for each
-- reservation. Each reservation can have only one bill
-- (enforced by the UNIQUE constraint on reservation_id).
-- =============================================================
CREATE TABLE IF NOT EXISTS bills (
    bill_id          INT AUTO_INCREMENT PRIMARY KEY,  -- bill_id: unique ID, auto-incremented by MySQL
    reservation_id   INT NOT NULL UNIQUE,             -- reservation_id: which reservation this bill belongs to (one bill per reservation)
    number_of_nights INT NOT NULL,                    -- number_of_nights: how many nights the guest stayed
    room_rate        DECIMAL(10,2) NOT NULL,          -- room_rate: the price per night at the time of billing
    subtotal         DECIMAL(10,2) NOT NULL,          -- subtotal: number_of_nights * room_rate (before tax and discount)
    tax_rate         DECIMAL(5,2) DEFAULT 10.00,      -- tax_rate: tax percentage (default 10%)
    tax_amount       DECIMAL(10,2) NOT NULL,          -- tax_amount: the calculated tax in LKR
    discount_percent DECIMAL(5,2) DEFAULT 0.00,       -- discount_percent: any discount applied (e.g., 5% loyalty discount)
    discount_amount  DECIMAL(10,2) DEFAULT 0.00,      -- discount_amount: the calculated discount in LKR
    total_amount     DECIMAL(10,2) NOT NULL,          -- total_amount: final amount the guest pays (subtotal + tax - discount)
    billing_strategy VARCHAR(50) DEFAULT 'STANDARD',  -- billing_strategy: which pricing strategy was used (STANDARD, SEASONAL, or LOYALTY)
    generated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- generated_at: when the bill was created
    generated_by     INT,                             -- generated_by: user_id of the staff member who generated the bill
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id), -- This links the bill to its reservation
    FOREIGN KEY (generated_by) REFERENCES users(user_id)                  -- This links the bill to the staff member who generated it
) ENGINE=InnoDB;

-- =============================================================
-- TABLE: audit_log
-- This table automatically records changes made to other tables.
-- It is populated by triggers (see below) and is used for
-- tracking who changed what and when.
-- =============================================================
CREATE TABLE IF NOT EXISTS audit_log (
    log_id      INT AUTO_INCREMENT PRIMARY KEY,              -- log_id: unique ID, auto-incremented by MySQL
    table_name  VARCHAR(50) NOT NULL,                        -- table_name: which table was changed (e.g., 'reservations')
    action_type ENUM('INSERT','UPDATE','DELETE') NOT NULL,   -- action_type: what kind of change happened
    record_id   INT NOT NULL,                                -- record_id: the primary key of the changed record
    old_values  TEXT,                                         -- old_values: the previous values before the change (NULL for INSERT)
    new_values  TEXT,                                         -- new_values: the new values after the change (NULL for DELETE)
    changed_by  VARCHAR(50),                                  -- changed_by: who made the change
    changed_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP           -- changed_at: when the change happened
) ENGINE=InnoDB;

-- =============================================================
-- STORED PROCEDURES
-- These are reusable SQL routines that the Java backend calls
-- to perform common database operations.
-- =============================================================

-- This stored procedure finds all available rooms for given dates.
-- It takes a check-in date, check-out date, and optional room type.
-- It returns rooms that are marked as available AND are not already
-- booked by another reservation during the requested date range.
DELIMITER //
CREATE PROCEDURE sp_get_available_rooms(IN p_in DATE, IN p_out DATE, IN p_type VARCHAR(20))
BEGIN
    SELECT r.* FROM rooms r WHERE r.is_available = TRUE
      AND (p_type IS NULL OR r.room_type = p_type)
      AND r.room_id NOT IN (
          SELECT res.room_id FROM reservations res
          WHERE res.status IN ('CONFIRMED','CHECKED_IN')
            AND res.check_in_date < p_out AND res.check_out_date > p_in);
END //
DELIMITER ;

-- This stored procedure generates an occupancy report for a date range.
-- It calculates how many rooms of each type were booked and what
-- percentage of total capacity was used (occupancy rate).
DELIMITER //
CREATE PROCEDURE sp_occupancy_report(IN p_start DATE, IN p_end DATE)
BEGIN
    SELECT r.room_type, COUNT(DISTINCT r.room_id) AS total_rooms,
           COUNT(DISTINCT res.reservation_id) AS total_bookings,
           ROUND((COUNT(DISTINCT res.reservation_id)*100.0)/NULLIF(COUNT(DISTINCT r.room_id)*DATEDIFF(p_end,p_start),0),2) AS occupancy_pct
    FROM rooms r LEFT JOIN reservations res ON r.room_id=res.room_id
        AND res.status IN ('CONFIRMED','CHECKED_IN','CHECKED_OUT')
        AND res.check_in_date < p_end AND res.check_out_date > p_start
    GROUP BY r.room_type;
END //
DELIMITER ;

-- This stored procedure generates a revenue report for a date range.
-- It shows how many bills were created for each room type, the total
-- revenue earned, and the average bill amount.
DELIMITER //
CREATE PROCEDURE sp_revenue_report(IN p_start DATE, IN p_end DATE)
BEGIN
    SELECT res.room_type, COUNT(b.bill_id) AS bills_count,
           COALESCE(SUM(b.total_amount),0) AS total_revenue,
           COALESCE(AVG(b.total_amount),0) AS avg_bill
    FROM bills b JOIN reservations res ON b.reservation_id=res.reservation_id
    WHERE b.generated_at BETWEEN p_start AND p_end GROUP BY res.room_type;
END //
DELIMITER ;

-- =============================================================
-- TRIGGERS
-- These run automatically when data in a table is inserted or
-- updated. They log changes to the audit_log table.
-- =============================================================

-- This trigger automatically logs new reservations to the audit_log table.
-- Whenever a new reservation is inserted, it records the guest name,
-- room ID, check-in date, and check-out date.
DELIMITER //
CREATE TRIGGER trg_reservation_insert AFTER INSERT ON reservations FOR EACH ROW
BEGIN
    INSERT INTO audit_log(table_name,action_type,record_id,new_values,changed_at)
    VALUES('reservations','INSERT',NEW.reservation_id,
           CONCAT('guest=',NEW.guest_name,',room=',NEW.room_id,',in=',NEW.check_in_date,',out=',NEW.check_out_date),NOW());
END //
DELIMITER ;

-- This trigger automatically logs status changes to the audit_log table.
-- Whenever a reservation is updated, it records the old status and the
-- new status so we can track the reservation lifecycle.
DELIMITER //
CREATE TRIGGER trg_reservation_update AFTER UPDATE ON reservations FOR EACH ROW
BEGIN
    INSERT INTO audit_log(table_name,action_type,record_id,old_values,new_values,changed_at)
    VALUES('reservations','UPDATE',NEW.reservation_id,CONCAT('status=',OLD.status),CONCAT('status=',NEW.status),NOW());
END //
DELIMITER ;

-- =============================================================
-- DEFAULT DATA
-- These INSERT statements add the initial data needed for the
-- system to work right after setup.
-- =============================================================

-- Default users - admin (password: 123) and staff1 (password: terry)
-- Passwords are stored as bcrypt hashes, not plain text, for security.
INSERT INTO users (username, password_hash, full_name, email, role) VALUES
('admin', '$2a$10$95U9YTNkHjTTjyNFW2epZOaG87vizKlKzDVVPONfp7SvA1MnUJfjC', 'System Administrator', 'admin@oceanview.lk', 'ADMIN'),
('staff1', '$2a$10$ZsDbWJXzUYi5iQ8iSZ7b0ee0NeGMb4Y.GwXAuhv2U.B2VdIyOwURK', 'Reception Staff', 'staff@oceanview.lk', 'STAFF');

-- 8 rooms: 3 standard, 3 deluxe, 2 suites (realistic Sri Lankan resort pricing in LKR)
-- Standard rooms : LKR  8,500 - 11,000 per night, max 2 guests
-- Deluxe rooms   : LKR 15,000 - 20,000 per night, max 3 guests
-- Suites         : LKR 35,000 - 55,000 per night, max 3-4 guests
INSERT INTO rooms (room_number, room_type, price_per_night, description, max_occupancy) VALUES
('STD-TW', 'STANDARD',  8500.00, 'Standard Room with Twin Beds',          2),
('STD-DBL','STANDARD',  8500.00, 'Standard Room with Double Bed',          2),
('STD-BAL','STANDARD', 11000.00, 'Standard Room with Balcony',             2),
('DLX-OV', 'DELUXE',  15000.00, 'Deluxe Ocean View Room',                 3),
('DLX-PV', 'DELUXE',  16000.00, 'Deluxe Pool View Room with Terrace',     3),
('DLX-JQ', 'DELUXE',  20000.00, 'Deluxe Room with Private Jacuzzi',       3),
('STE-JR', 'SUITE',   35000.00, 'Junior Suite with Ocean Panorama',        3),
('STE-PR', 'SUITE',   55000.00, 'Presidential Suite with Private Pool',    4);
