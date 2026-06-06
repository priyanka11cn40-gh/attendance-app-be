package com.example.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MissedCheckoutEmployeeDto {

    private Long attendanceId;
    private String empId;
    private String name;
    private String email;
}
