package com.oceanview.dao;

import com.oceanview.model.Room;
import java.time.LocalDate;
import java.util.List;

/**
 * RoomDAO - This is an interface for Room data access.
 * An interface defines what methods the implementation class must have,
 * but does not contain the actual code for those methods.
 *
 * PATTERN: DAO (Data Access Object) - separates database code from business logic.
 * Includes a method that calls the stored procedure sp_get_available_rooms.
 */
public interface RoomDAO {

    // This method adds a new room to the database and returns true if successful
    boolean addRoom(Room room);

    // This method finds and returns a room by its unique ID number
    Room getRoomById(int id);

    // This method finds and returns a room by its room number (e.g., "101")
    Room getRoomByNumber(String number);

    // This method returns a list of all rooms in the hotel
    List<Room> getAllRooms();

    // This method finds rooms that are available between the given dates and optionally by type
    // It calls a stored procedure in the database to do the work
    List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, String type);

    // This method updates an existing room's details and returns true if successful
    boolean updateRoom(Room room);
}
