package com.example.attendance.controller;

import com.example.attendance.dto.*;
import com.example.attendance.entity.Users;
import com.example.attendance.repository.UserRepository;
import com.example.attendance.service.UserService;
import com.example.attendance.util.JwtUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    private final UserRepository repository;
    private final JwtUtility jwtUtil;
    private final BCryptPasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {

        String userId = request.getUserId().trim();
        String password = request.getPassword().trim();

        Users employee = repository.findByUserId(userId).orElse(null);

        if (employee == null) {
            log.warn("Login failed - Invalid User ID: {}", userId);
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid User ID"));
        }

        if (Boolean.FALSE.equals(employee.getActive())) {
            log.warn("Login failed - User Disabled: {}", userId);
            return ResponseEntity.badRequest().body(Map.of("message", "User Disabled"));
        }

        if (!encoder.matches(password, employee.getPassword())) {
            log.warn("Login failed - Invalid Password for user: {}", userId);
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Password"));
        }

        String token = jwtUtil.generateToken(employee.getUserId());

        log.info("Login successful for user: {}", userId);

        return ResponseEntity.ok(
                new LoginResponseDto(
                        employee.getName(),
                        token,
                        employee.getUserId(),
                        "Login Successful"
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody String userId) {

        log.info("Logout successful for user: {}", userId.trim());

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request) {

        return ResponseEntity.ok(Map.of("message", userService.changePassword(request)));
    }

    // GET SECURITY QUESTION
    @PostMapping("/forgot-password/question")
    public ResponseEntity<?> getQuestion(
            @RequestBody ForgotPasswordRequest request) {

        String question = userService.getSecurityQuestion(
                request.getUserId()
        );

        return ResponseEntity.ok(
                Map.of("securityQuestion", question)
        );
    }

    @PostMapping("/forgot-password/question/verify")
    public ResponseEntity<?> verifyAnswer(
            @RequestBody VerifySecretAnswerRequest request) {

        boolean verified =
                userService.verifySecurityAnswer(
                        request
                );

        return ResponseEntity.ok(
                Map.of(
                        "verified",
                        verified
                )
        );
    }

    // RESET PASSWORD
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(
            @RequestBody ChangePasswordRequest request) {

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        userService.resetPassword(request)
                )
        );
    }
}
