package com.example.attendance.controller;

import com.example.attendance.service.AttendanceMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AttendanceMailService attendanceMailService;

    @GetMapping("/mail")
    public String testMail() {
        System.out.println("Inside---------------");
        attendanceMailService.sendDailyAttendanceMail();
        return "Mail Sent";
    }
}
