package com.example.attendance.dto;

import lombok.Data;

@Data
public class AttendanceRequestDto {

    private String userId;
    private double latitude;
    private double longitude;
}
