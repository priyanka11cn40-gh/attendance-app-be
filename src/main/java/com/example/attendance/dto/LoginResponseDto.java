package com.example.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String name;
    private String token;
    private String userId;
    private String message;
}

