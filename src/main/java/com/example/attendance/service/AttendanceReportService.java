package com.example.attendance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AttendanceReportService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendDailyAttendanceReport() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo("nsvsrikar@gmail.com");   // receiver
            message.setSubject("Daily Attendance Report");
            message.setText("Attendance report generated successfully.");

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
