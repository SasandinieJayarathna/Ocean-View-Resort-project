package com.oceanview.model;

// ============================================================
// SuiteRoom.java
// This file defines the SuiteRoom class, which is the premium
// room option with a living area and panoramic ocean view.
// It extends (inherits from) the Room class, so it gets all
// the fields and methods from Room automatically.
// The room type is always set to "SUITE" for this class.
// ============================================================

/**
 * Suite room — premium with living area and panoramic ocean view.
 */
public class SuiteRoom extends Room {

    // Default constructor — calls the parent (Room) constructor
    // and sets the room type to "SUITE".
    public SuiteRoom() {
        super();               // call the Room default constructor
        setRoomType("SUITE");  // mark this room as a suite room
    }
}
