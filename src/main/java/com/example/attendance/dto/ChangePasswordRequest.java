package com.example.attendance.dto;

import lombok.Data;
@Data
public class ChangePasswordRequest {
    private String userId;
    private String newPassword;
    private String securityQuestion;
    private String securityAnswer;
}
