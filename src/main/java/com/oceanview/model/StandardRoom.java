package com.oceanview.model;

// ============================================================
// StandardRoom.java
// This file defines the StandardRoom class, which is the most
// affordable room option at Ocean View Resort.
// It extends (inherits from) the Room class, so it gets all
// the fields and methods from Room automatically.
// The room type is always set to "STANDARD" for this class.
// ============================================================

/**
 * Standard room — most affordable option at Ocean View Resort.
 */
public class StandardRoom extends Room {

    // Default constructor — calls the parent (Room) constructor
    // and sets the room type to "STANDARD".
    public StandardRoom() {
        super();                  // call the Room default constructor
        setRoomType("STANDARD");  // mark this room as a standard room
    }
}
