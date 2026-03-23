package model;

public class Room {
    private String roomNumber;
    private String type; // Classroom / Lab

    public Room(String roomNumber, String type) {
        this.roomNumber = roomNumber;
        this.type = type;
    }

    public String getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
}