package com.example.attendance.controller;

import com.example.attendance.service.AttendanceAutoCheckoutService;
import com.example.attendance.service.AttendanceMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AttendanceMailService attendanceMailService;

    @Autowired
    private AttendanceAutoCheckoutService attendanceAutoCheckoutService;

    @GetMapping("/mail")
    public ResponseEntity<?> testMail() {
        System.out.println("Inside---------------");
        attendanceMailService.sendWeeklyAttendanceMail();
        return ResponseEntity.ok(Map.of("message", "Mail Sent"));
    }

    @GetMapping("/auto-checkout")
    public ResponseEntity<?> testAutoCheckout() {
        int processedCount = attendanceAutoCheckoutService.processMissedCheckoutsForPreviousDay();
        return ResponseEntity.ok(Map.of(
                "message", "Auto checkout job completed",
                "processedCount", processedCount
        ));
    }

    @GetMapping("/monthly-mail")
    public ResponseEntity<?> testMonthlyMail() {
        attendanceMailService.sendMonthlyAttendanceMail();
        return ResponseEntity.ok(Map.of("message", "Monthly mail sent with live attendance data"));
    }
}
