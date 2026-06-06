package com.example.attendance.dto;

import lombok.Data;
@Data
public class VerifySecretAnswerRequest {
    private String userId;
    private String securityAnswer;
}
