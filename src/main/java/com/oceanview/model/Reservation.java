package com.oceanview.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// ============================================================
// Reservation.java
// This file defines the Reservation class, which represents a
// guest's booking at Ocean View Resort. It stores all the
// details about the guest, which room they booked, and the
// check-in/check-out dates. It also has a method to calculate
// how many nights the guest is staying.
// A Bill is linked to a Reservation — a bill can't exist
// without a reservation (this is called Composition).
// ============================================================

/**
 * Reservation — Guest booking record at Ocean View Resort.
 * KEY METHOD: getNumberOfNights() calculates stay duration.
 * RELATIONSHIP: Composition with Bill (Bill cannot exist without Reservation).
 */
public class Reservation {

    // These private fields store all the booking information.
    // They are private for encapsulation — we use getters and setters to access them.
    private int reservationId;          // unique ID for this reservation in the database
    private String reservationNumber;   // a human-readable reservation number (e.g. "RES-001")
    private String guestName;           // the name of the guest who made the booking
    private String guestAddress;        // the guest's home address
    private String contactNumber;       // the guest's phone number
    private String guestEmail;          // the guest's email address
    private int roomId;                 // the ID of the room that was booked
    private String roomType;            // what type of room was booked (e.g. "DELUXE")
    private LocalDate checkInDate;      // the date the guest checks in
    private LocalDate checkOutDate;     // the date the guest checks out
    private String status;              // the booking status, e.g. "CONFIRMED" or "CANCELLED"
    private String specialRequests;     // any special requests from the guest (e.g. "extra pillows")
    private int createdBy;              // the user ID of the staff member who created this booking
    private LocalDateTime createdAt;    // the date and time the reservation was created

    // Default constructor — sets the status to "CONFIRMED" by default
    // because when we first create a reservation, it's confirmed.
    public Reservation() {
        this.status = "CONFIRMED";
    }

    // This method calculates how many nights the guest is staying.
    // It uses ChronoUnit.DAYS.between() to count the days between check-in and check-out.
    // For example, if check-in is Jan 1 and check-out is Jan 3, that's 2 nights.
    // If either date is missing (null), it returns 0 to avoid errors.
    public long getNumberOfNights() {
        if (checkInDate == null || checkOutDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    // =====================================================
    // Getters and setters
    // These let other classes read and change the private
    // fields above. Each field has a get (read) and set
    // (write) method — this is standard Java encapsulation.
    // =====================================================

    // getter for reservationId — returns the database ID
    public int getReservationId() {
        return reservationId;
    }

    // setter for reservationId — sets the database ID
    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    // getter for reservationNumber — returns the readable booking number
    public String getReservationNumber() {
        return reservationNumber;
    }

    // setter for reservationNumber — sets the readable booking number
    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    // getter for guestName — returns the guest's name
    public String getGuestName() {
        return guestName;
    }

    // setter for guestName — updates the guest's name
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    // getter for guestAddress — returns the guest's address
    public String getGuestAddress() {
        return guestAddress;
    }

    // setter for guestAddress — updates the guest's address
    public void setGuestAddress(String guestAddress) {
        this.guestAddress = guestAddress;
    }

    // getter for contactNumber — returns the guest's phone number
    public String getContactNumber() {
        return contactNumber;
    }

    // setter for contactNumber — updates the guest's phone number
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    // getter for guestEmail — returns the guest's email
    public String getGuestEmail() {
        return guestEmail;
    }

    // setter for guestEmail — updates the guest's email
    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    // getter for roomId — returns the ID of the booked room
    public int getRoomId() {
        return roomId;
    }

    // setter for roomId — sets which room is booked
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    // getter for roomType — returns the type of room booked
    public String getRoomType() {
        return roomType;
    }

    // setter for roomType — sets the type of room booked
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    // getter for checkInDate — returns when the guest arrives
    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    // setter for checkInDate — sets when the guest arrives
    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    // getter for checkOutDate — returns when the guest leaves
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    // setter for checkOutDate — sets when the guest leaves
    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    // getter for status — returns the booking status (e.g. "CONFIRMED")
    public String getStatus() {
        return status;
    }

    // setter for status — lets us update the booking status (e.g. to "CANCELLED")
    public void setStatus(String status) {
        this.status = status;
    }

    // getter for specialRequests — returns any special requests the guest made
    public String getSpecialRequests() {
        return specialRequests;
    }

    // setter for specialRequests — lets us record special requests
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    // getter for createdBy — returns the ID of the staff member who made this booking
    public int getCreatedBy() {
        return createdBy;
    }

    // setter for createdBy — records which staff member created this booking
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    // getter for createdAt — returns when this reservation was created
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // setter for createdAt — sets the creation timestamp
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString method — gives a readable summary when we print a Reservation object.
    // It shows the reservation number, guest name, and the stay dates.
    @Override
    public String toString() {
        return "Reservation{num='" + reservationNumber + "', guest='" + guestName + "', " + checkInDate + " to " + checkOutDate + "}";
    }
}
