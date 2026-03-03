package com.oceanview.model;

/**
 * Room — Abstract base class for hotel rooms.
 * PATTERN: Factory pattern creates subtypes via RoomFactory.
 * SOLID: Liskov Substitution — all subtypes substitute for Room.
 */
public abstract class Room {
    private int roomId;
    private String roomNumber;
    private String roomType;
    private double pricePerNight;
    private boolean isAvailable;
    private String description;
    private int maxOccupancy;

    public Room() { this.isAvailable = true; }

    public Room(String roomNumber, String roomType, double pricePerNight, String description, int maxOccupancy) {
        this.roomNumber = roomNumber; this.roomType = roomType; this.pricePerNight = pricePerNight;
        this.description = description; this.maxOccupancy = maxOccupancy; this.isAvailable = true;
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getMaxOccupancy() { return maxOccupancy; }
    public void setMaxOccupancy(int maxOccupancy) { this.maxOccupancy = maxOccupancy; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{room='" + roomNumber + "', type='" + roomType + "', price=" + pricePerNight + "}";
    }
}
