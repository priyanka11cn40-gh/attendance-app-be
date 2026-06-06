package com.example.attendance.controller;

import com.example.attendance.dto.AttendanceRequestDto;
import com.example.attendance.dto.LogoutChekoutRequest;
import com.example.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/hello")
    public ResponseEntity<?> hello(){
        return ResponseEntity.ok(Map.of("message", "Hello from attendance app"));
    }

    @PostMapping("/checkin")
    public ResponseEntity<?> checkIn(@RequestBody AttendanceRequestDto req) {

        return ResponseEntity.ok(Map.of(
                "message",
                attendanceService.checkIn(req.getUserId(),
                        req.getLatitude(),
                        req.getLongitude())
        ));
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkOut(@RequestBody AttendanceRequestDto req) {
        return ResponseEntity.ok(Map.of(
                "message",
                attendanceService.checkOut(req.getUserId(),
                        req.getLatitude(),
                        req.getLongitude())
        ));
    }

    @PostMapping("/logout-checkout")
    public ResponseEntity<?> logoutCheckout(@RequestBody LogoutChekoutRequest request) {

        String response = attendanceService.logoutAndCheckout(request);

        Map<String, Object> result = new HashMap<>();

        result.put("message", response);
        result.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(result);
    }
}
