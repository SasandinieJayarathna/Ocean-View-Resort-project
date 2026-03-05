package com.oceanview.model;

// ============================================================
// Room.java
// This file defines the Room class, which is the parent class
// for all types of hotel rooms (Standard, Deluxe, Suite).
// It is abstract, meaning we can't create a Room object directly —
// we have to create a StandardRoom, DeluxeRoom, or SuiteRoom instead.
// A Factory pattern is used elsewhere to create the right subtype.
// ============================================================

/**
 * Room — Abstract base class for hotel rooms.
 * PATTERN: Factory pattern creates subtypes via RoomFactory.
 * SOLID: Liskov Substitution — all subtypes substitute for Room.
 */
public abstract class Room {

    // These are the private fields that store room information.
    // They are private for encapsulation — other classes use getters/setters to access them.
    private int roomId;            // unique ID for this room in the database
    private String roomNumber;     // descriptive code, e.g. "STD-TW", "DLX-OV"
    private String roomType;       // the type of room: "STANDARD", "DELUXE", or "SUITE"
    private double pricePerNight;  // Room Only (RO) rate per night in LKR
    private double bbPrice;        // Bed & Breakfast rate per night in LKR
    private double hbPrice;        // Half Board (breakfast + dinner) rate per night in LKR
    private double fbPrice;        // Full Board (all meals included) rate per night in LKR
    private boolean isAvailable;   // true if the room is free to book, false if occupied
    private String description;    // a short description of the room's features
    private int maxOccupancy;      // the maximum number of guests allowed in this room

    // Default constructor — sets the room as available by default.
    public Room() {
        this.isAvailable = true;
    }

    // Parameterized constructor — lets us create a room with all its details at once.
    // The room is set to available by default when it's first created.
    public Room(String roomNumber, String roomType, double pricePerNight, String description, int maxOccupancy) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.maxOccupancy = maxOccupancy;
        this.isAvailable = true; // new rooms are available by default
    }

    // =====================================================
    // Getters and setters
    // These let other classes read and change the private
    // fields above. This is encapsulation in action.
    // =====================================================

    // getter for roomId — returns the room's unique database ID
    public int getRoomId() {
        return roomId;
    }

    // setter for roomId — lets us assign a database ID to this room
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    // getter for roomNumber — returns the room number (e.g. "101")
    public String getRoomNumber() {
        return roomNumber;
    }

    // setter for roomNumber — lets us set or change the room number
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    // getter for roomType — returns the type like "STANDARD", "DELUXE", or "SUITE"
    public String getRoomType() {
        return roomType;
    }

    // setter for roomType — lets us change the room type
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    // getter for pricePerNight — returns the Room Only (RO) nightly rate
    public double getPricePerNight() {
        return pricePerNight;
    }

    // setter for pricePerNight — lets us update the Room Only nightly rate
    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    // getter for bbPrice — returns the Bed & Breakfast rate per night
    public double getBbPrice() {
        return bbPrice;
    }

    // setter for bbPrice — lets us set the Bed & Breakfast nightly rate
    public void setBbPrice(double bbPrice) {
        this.bbPrice = bbPrice;
    }

    // getter for hbPrice — returns the Half Board (breakfast + dinner) rate per night
    public double getHbPrice() {
        return hbPrice;
    }

    // setter for hbPrice — lets us set the Half Board nightly rate
    public void setHbPrice(double hbPrice) {
        this.hbPrice = hbPrice;
    }

    // getter for fbPrice — returns the Full Board (all meals) rate per night
    public double getFbPrice() {
        return fbPrice;
    }

    // setter for fbPrice — lets us set the Full Board nightly rate
    public void setFbPrice(double fbPrice) {
        this.fbPrice = fbPrice;
    }

    // getter for isAvailable — returns true if the room can be booked
    public boolean isAvailable() {
        return isAvailable;
    }

    // setter for isAvailable — lets us mark the room as available or occupied
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // getter for description — returns the room's feature description
    public String getDescription() {
        return description;
    }

    // setter for description — lets us update the room description
    public void setDescription(String description) {
        this.description = description;
    }

    // getter for maxOccupancy — returns the max number of guests allowed
    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    // setter for maxOccupancy — lets us change the max guest limit
    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    // toString method — gives a readable string when we print a Room object.
    // It shows the actual class name (StandardRoom, etc.), room number, type, and price.
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{room='" + roomNumber + "', type='" + roomType + "', price=" + pricePerNight + "}";
    }
}
