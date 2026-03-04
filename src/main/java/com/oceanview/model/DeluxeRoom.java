package com.oceanview.model;

// ============================================================
// DeluxeRoom.java
// This file defines the DeluxeRoom class, which is the mid-tier
// room option with an ocean view and balcony.
// It extends (inherits from) the Room class, so it gets all
// the fields and methods from Room automatically.
// The room type is always set to "DELUXE" for this class.
// ============================================================

/**
 * Deluxe room — mid-tier with ocean view and balcony.
 */
public class DeluxeRoom extends Room {

    // Default constructor — calls the parent (Room) constructor
    // and sets the room type to "DELUXE".
    public DeluxeRoom() {
        super();                // call the Room default constructor
        setRoomType("DELUXE");  // mark this room as a deluxe room
    }
}
