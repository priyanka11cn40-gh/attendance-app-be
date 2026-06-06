package com.example.attendance.scheduler;

import com.example.attendance.service.AttendanceAutoCheckoutService;
import com.example.attendance.service.AttendanceMailService;
import com.example.attendance.util.WorkingCalendarUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceScheduler {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private final AttendanceMailService attendanceMailService;
    private final AttendanceAutoCheckoutService attendanceAutoCheckoutService;

//    @EventListener(ApplicationReadyEvent.class)
//    public void sendReportOnStartup() {
//        sendReport("application startup");
//    }

    @Scheduled(cron = "0 0 18 * * SAT", zone = "Asia/Kolkata")
    public void sendWeeklyReportOnSaturday() {
        sendReport("Saturday end-of-week cron");
    }

    @Scheduled(cron = "0 0 18 * * *", zone = "Asia/Kolkata")
    public void sendMonthlyReportOnLastWorkingDay() {
        LocalDate today = LocalDate.now(IST);
        if (!WorkingCalendarUtil.isLastWorkingDayOfMonth(today)) {
            return;
        }

        log.info("Monthly attendance mail triggered on last working day: {}", today);
        try {
            attendanceMailService.sendMonthlyAttendanceMail();
            log.info("Monthly attendance mail completed for {}", today);
        } catch (Exception ex) {
            log.error("Monthly attendance mail failed for {}", today, ex);
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Kolkata")
    public void processMissedCheckouts() {
        log.info("Missed check-out job triggered at midnight IST");
        try {
            int processedCount = attendanceAutoCheckoutService.processMissedCheckoutsForPreviousDay();
            log.info("Missed check-out job completed. Processed {} record(s)", processedCount);
        } catch (Exception ex) {
            log.error("Missed check-out job failed", ex);
        }
    }

    private void sendReport(String trigger) {
        log.info("Attendance mail triggered by {}", trigger);
        try {
            attendanceMailService.sendWeeklyAttendanceMail();
            log.info("Attendance mail completed for {}", trigger);
        } catch (Exception ex) {
            log.error("Attendance mail failed for {}", trigger, ex);
        }
    }
}
