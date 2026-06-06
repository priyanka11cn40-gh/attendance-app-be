package com.example.attendance.dto;

import lombok.Data;
@Data
public class ForgotPasswordResetRequest {
    private String userId;
    private String securityAnswer;
    private String newPassword;

}
