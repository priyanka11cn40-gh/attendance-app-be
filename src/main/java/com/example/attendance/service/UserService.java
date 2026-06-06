package com.example.attendance.service;

import com.example.attendance.dto.ChangePasswordRequest;
import com.example.attendance.dto.ForgotPasswordResetRequest;
import com.example.attendance.dto.VerifySecretAnswerRequest;
import com.example.attendance.entity.Sites;
import com.example.attendance.entity.UserAttendance;
import com.example.attendance.entity.Users;
import com.example.attendance.repository.SiteRepository;
import com.example.attendance.repository.UserAttendanceRepository;
import com.example.attendance.repository.UserRepository;
import com.example.attendance.util.LocationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // CHANGE PASSWORD
    public String changePassword(ChangePasswordRequest request) {

        Users user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        user.setSecurityQuestion(request.getSecurityQuestion());

        user.setSecurityAnswer(
                passwordEncoder.encode(request.getSecurityAnswer())
        );

        userRepository.save(user);

        return "Password updated successfully";
    }

    // FETCH SECURITY QUESTION
    public String getSecurityQuestion(String userId) {

        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSecurityQuestion();
    }

    public String resetPassword(
            ChangePasswordRequest request) {

        Users user = userRepository.findById(
                request.getUserId()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        // UPDATE PASSWORD
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        // OPTIONAL UPDATE SECURITY QUESTION
        if (request.getSecurityQuestion() != null
                && request.getSecurityAnswer() != null) {

            user.setSecurityQuestion(
                    request.getSecurityQuestion()
            );

            user.setSecurityAnswer(
                    passwordEncoder.encode(
                            request.getSecurityAnswer()
                    )
            );
        }

        userRepository.save(user);

        return "Password reset successful";
    }
    public boolean verifySecurityAnswer(
            VerifySecretAnswerRequest request) {

        Users user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return passwordEncoder.matches(
                request.getSecurityAnswer(),
                user.getSecurityAnswer()
        );
    }
}