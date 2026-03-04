package com.oceanview.dao;

import com.oceanview.model.Room;
import java.time.LocalDate;
import java.util.List;

/**
 * RoomDAO — Data access interface for Room operations.
 * Includes method for calling stored procedure sp_get_available_rooms.
 */
public interface RoomDAO {
    boolean addRoom(Room room);
    Room getRoomById(int id);
    Room getRoomByNumber(String number);
    List<Room> getAllRooms();
    List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, String type);
    boolean updateRoom(Room room);
}
