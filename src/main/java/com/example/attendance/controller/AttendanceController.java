package com.example.attendance.controller;

import com.example.attendance.dto.AttendanceRequestDto;
import com.example.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/hello")
    public String hello(){
        return "Hello from attendance app";
    }

    @PostMapping("/checkin")
    public ResponseEntity<String> checkIn(@RequestBody AttendanceRequestDto req) {

        return ResponseEntity.ok(
                attendanceService.checkIn(req.getUserId(),
                        req.getLatitude(),
                        req.getLongitude())
        );
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkOut(@RequestBody AttendanceRequestDto req) {
        return ResponseEntity.ok(
                attendanceService.checkOut(req.getUserId(),
                        req.getLatitude(),
                        req.getLongitude())
        );
    }
}