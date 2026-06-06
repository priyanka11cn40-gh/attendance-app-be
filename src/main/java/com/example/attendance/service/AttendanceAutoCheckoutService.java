package com.example.attendance.service;

import com.example.attendance.dto.MissedCheckoutEmployeeDto;
import com.example.attendance.entity.UserAttendance;
import com.example.attendance.repository.UserAttendanceRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceAutoCheckoutService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final String HR_EMAIL = "admin@efcoindia.com";
    private static final String SYSTEM_LOGOUT_REASON = "SYSTEM_AUTO_CHECKOUT";
    private static final DateTimeFormatter REPORT_DATE = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    private final UserAttendanceRepository attendanceRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public int processMissedCheckoutsForPreviousDay() {
        LocalDate previousDay = LocalDate.now(IST).minusDays(1);
        LocalDateTime autoCheckoutUtc = previousDay.atTime(23, 59, 59)
                .atZone(IST)
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();

        List<Object[]> missedCheckoutRows = attendanceRepository.findMissedCheckoutsForDate(previousDay);
        if (missedCheckoutRows.isEmpty()) {
            log.info("No missed check-outs found for {}", previousDay);
            return 0;
        }

        List<MissedCheckoutEmployeeDto> affectedEmployees = new ArrayList<>();

        for (Object[] row : missedCheckoutRows) {
            Long attendanceId = ((Number) row[0]).longValue();
            String empId = row[1] == null ? "" : row[1].toString();
            String name = row[2] == null ? "" : row[2].toString();
            String email = row[3] == null ? "" : row[3].toString();

            UserAttendance attendance = attendanceRepository.findById(attendanceId)
                    .orElseThrow(() -> new IllegalStateException("Attendance record not found: " + attendanceId));

            attendance.setCheckOutTime(autoCheckoutUtc);
            attendance.setLogoutReason(SYSTEM_LOGOUT_REASON);
            attendanceRepository.save(attendance);

            affectedEmployees.add(new MissedCheckoutEmployeeDto(attendanceId, empId, name, email));
            log.info("Auto check-out applied for empId={} on {}", empId, previousDay);
        }

        sendMissedCheckoutNotification(previousDay, affectedEmployees);
        return affectedEmployees.size();
    }

    private void sendMissedCheckoutNotification(LocalDate attendanceDate, List<MissedCheckoutEmployeeDto> employees) {
        String formattedDate = attendanceDate.format(REPORT_DATE);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);

            helper.setTo(HR_EMAIL);
            helper.setSubject("Missed Check-Out Alert - " + formattedDate);
            helper.setText(buildNotificationBody(formattedDate, employees), false);

            mailSender.send(message);
            log.info("Missed check-out notification sent to HR for {} employee(s) on {}", employees.size(), formattedDate);
        } catch (Exception ex) {
            log.error("Failed to send missed check-out notification for {}", formattedDate, ex);
            throw new IllegalStateException("Failed to send missed check-out notification", ex);
        }
    }

    private String buildNotificationBody(String formattedDate, List<MissedCheckoutEmployeeDto> employees) {
        StringBuilder body = new StringBuilder();
        body.append("The following employee(s) checked in on ")
                .append(formattedDate)
                .append(" but did not check out.")
                .append(System.lineSeparator())
                .append("A system-generated check-out was applied at 23:59:59 IST for that day.")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        for (MissedCheckoutEmployeeDto employee : employees) {
            body.append("Emp ID: ")
                    .append(employee.getEmpId())
                    .append(" | Email: ")
                    .append(displayEmail(employee.getEmail()))
                    .append(" | Name: ")
                    .append(employee.getName())
                    .append(System.lineSeparator());
        }

        return body.toString();
    }

    private String displayEmail(String email) {
        return email == null || email.isBlank() ? "Not available" : email.trim();
    }
}
