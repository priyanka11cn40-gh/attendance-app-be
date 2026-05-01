package com.example.attendance.dto;

public class AttendanceMailDto {

    private String userId;
    private String name;
    private String status;
    private String checkIn;
    private String checkOut;

    public AttendanceMailDto(String userId, String name, String status, String checkIn, String checkOut) {
        this.userId = userId;
        this.name = name;
        this.status = status;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
}