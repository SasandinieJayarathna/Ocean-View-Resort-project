package com.oceanview.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Reservation — Guest booking record at Ocean View Resort.
 * KEY METHOD: getNumberOfNights() calculates stay duration.
 * RELATIONSHIP: Composition with Bill (Bill cannot exist without Reservation — ◆ in class diagram).
 */
public class Reservation {
    private int reservationId;
    private String reservationNumber;
    private String guestName;
    private String guestAddress;
    private String contactNumber;
    private String guestEmail;
    private int roomId;
    private String roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private String specialRequests;
    private int createdBy;
    private LocalDateTime createdAt;

    public Reservation() { this.status = "CONFIRMED"; }

    /** Calculates nights between check-in and check-out using Java 8 ChronoUnit. */
    public long getNumberOfNights() {
        if (checkInDate == null || checkOutDate == null) return 0;
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestAddress() { return guestAddress; }
    public void setGuestAddress(String guestAddress) { this.guestAddress = guestAddress; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Reservation{num='" + reservationNumber + "', guest='" + guestName + "', " + checkInDate + " to " + checkOutDate + "}";
    }
}
