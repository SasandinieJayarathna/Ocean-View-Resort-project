package com.oceanview.pattern.factory;

import com.oceanview.model.*;

// This is the Factory pattern - it creates different types of rooms based on a string
// Instead of writing "new StandardRoom()" everywhere in our code, we just call this one method
// and pass in what type of room we want. This keeps all room creation in one place.
public class RoomFactory {

    // This method is static so we can call it without creating a RoomFactory object
    // We just say RoomFactory.createRoom("DELUXE") and it gives us the right room
    public static Room createRoom(String type) {

        // First we check if someone passed in null - if so, throw an error right away
        if (type == null) throw new IllegalArgumentException("Room type cannot be null");

        // The switch statement checks which room type was requested
        // We convert to uppercase and trim whitespace so "deluxe", "DELUXE", " Deluxe " all work
        switch (type.toUpperCase().trim()) {
            case "STANDARD": return new StandardRoom();   // Creates a basic standard room
            case "DELUXE":   return new DeluxeRoom();     // Creates a fancier deluxe room
            case "SUITE":    return new SuiteRoom();      // Creates the top-tier suite room
            // If someone passes in a type we don't know, we throw an error to let them know
            default: throw new IllegalArgumentException("Unknown room type: " + type);
        }
    }
}
