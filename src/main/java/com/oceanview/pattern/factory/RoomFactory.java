package com.oceanview.pattern.factory;

import com.oceanview.model.*;

/**
 * RoomFactory — Factory Method pattern for creating Room subtypes.
 * PATTERN: Factory (Creational). Centralizes room object creation.
 * SOLID: Open-Closed — add new room type by adding case, existing callers unchanged.
 */
public class RoomFactory {
    public static Room createRoom(String type) {
        if (type == null) throw new IllegalArgumentException("Room type cannot be null");
        switch (type.toUpperCase().trim()) {
            case "STANDARD": return new StandardRoom();
            case "DELUXE":   return new DeluxeRoom();
            case "SUITE":    return new SuiteRoom();
            default: throw new IllegalArgumentException("Unknown room type: " + type);
        }
    }
}
