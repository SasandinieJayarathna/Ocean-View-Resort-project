CREATE DATABASE IF NOT EXISTS oceanview_resort;
USE oceanview_resort;

CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(100),
    role          ENUM('STAFF','ADMIN') NOT NULL DEFAULT 'STAFF',
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS rooms (
    room_id         INT AUTO_INCREMENT PRIMARY KEY,
    room_number     VARCHAR(10) NOT NULL UNIQUE,
    room_type       ENUM('STANDARD','DELUXE','SUITE') NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    is_available    BOOLEAN DEFAULT TRUE,
    description     TEXT,
    max_occupancy   INT DEFAULT 2,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reservations (
    reservation_id     INT AUTO_INCREMENT PRIMARY KEY,
    reservation_number VARCHAR(20) NOT NULL UNIQUE,
    guest_name         VARCHAR(100) NOT NULL,
    guest_address      VARCHAR(255),
    contact_number     VARCHAR(20) NOT NULL,
    guest_email        VARCHAR(100),
    room_id            INT NOT NULL,
    room_type          ENUM('STANDARD','DELUXE','SUITE') NOT NULL,
    check_in_date      DATE NOT NULL,
    check_out_date     DATE NOT NULL,
    status             ENUM('CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    special_requests   TEXT,
    created_by         INT,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT chk_dates CHECK (check_out_date > check_in_date)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bills (
    bill_id          INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id   INT NOT NULL UNIQUE,
    number_of_nights INT NOT NULL,
    room_rate        DECIMAL(10,2) NOT NULL,
    subtotal         DECIMAL(10,2) NOT NULL,
    tax_rate         DECIMAL(5,2) DEFAULT 10.00,
    tax_amount       DECIMAL(10,2) NOT NULL,
    discount_percent DECIMAL(5,2) DEFAULT 0.00,
    discount_amount  DECIMAL(10,2) DEFAULT 0.00,
    total_amount     DECIMAL(10,2) NOT NULL,
    billing_strategy VARCHAR(50) DEFAULT 'STANDARD',
    generated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    generated_by     INT,
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),
    FOREIGN KEY (generated_by) REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS audit_log (
    log_id      INT AUTO_INCREMENT PRIMARY KEY,
    table_name  VARCHAR(50) NOT NULL,
    action_type ENUM('INSERT','UPDATE','DELETE') NOT NULL,
    record_id   INT NOT NULL,
    old_values  TEXT,
    new_values  TEXT,
    changed_by  VARCHAR(50),
    changed_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

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

DELIMITER //
CREATE TRIGGER trg_reservation_insert AFTER INSERT ON reservations FOR EACH ROW
BEGIN
    INSERT INTO audit_log(table_name,action_type,record_id,new_values,changed_at)
    VALUES('reservations','INSERT',NEW.reservation_id,
           CONCAT('guest=',NEW.guest_name,',room=',NEW.room_id,',in=',NEW.check_in_date,',out=',NEW.check_out_date),NOW());
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER trg_reservation_update AFTER UPDATE ON reservations FOR EACH ROW
BEGIN
    INSERT INTO audit_log(table_name,action_type,record_id,old_values,new_values,changed_at)
    VALUES('reservations','UPDATE',NEW.reservation_id,CONCAT('status=',OLD.status),CONCAT('status=',NEW.status),NOW());
END //
DELIMITER ;

INSERT INTO users (username, password_hash, full_name, email, role) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', 'admin@oceanview.lk', 'ADMIN'),
('staff1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Reception Staff', 'staff@oceanview.lk', 'STAFF');

INSERT INTO rooms (room_number, room_type, price_per_night, description, max_occupancy) VALUES
('101','STANDARD',5000.00,'Standard room with garden view',2),
('102','STANDARD',5000.00,'Standard room with garden view',2),
('103','STANDARD',5500.00,'Standard room with partial ocean view',2),
('201','DELUXE',10000.00,'Deluxe room with full ocean view and balcony',3),
('202','DELUXE',10000.00,'Deluxe room with full ocean view and balcony',3),
('203','DELUXE',12000.00,'Deluxe room with private terrace and jacuzzi',3),
('301','SUITE',20000.00,'Presidential suite with living area and ocean panorama',4),
('302','SUITE',18000.00,'Executive suite with office space and ocean view',4);
