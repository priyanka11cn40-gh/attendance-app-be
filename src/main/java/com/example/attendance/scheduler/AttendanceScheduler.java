package com.example.attendance.scheduler;

import com.example.attendance.service.AttendanceMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttendanceScheduler {

    private final AttendanceMailService attendanceMailService;

    @Scheduled(cron = "0 0 * * * *")
    public void sendReport() {
        attendanceMailService.sendDailyAttendanceMail();
    }
}
