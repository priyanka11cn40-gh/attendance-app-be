package com.example.attendance.service;

import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.util.AttendanceReportExcelBuilder;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceMailService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter MONTH_DAY = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH);
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
    private static final String[] REPORT_RECIPIENTS = {
            "admin@efcoindia.com",
            "nsr@efcoindia.com",
            "tekadmin@teklancesol.com"
    };

    private final AttendanceRepository attendanceRepository;
    private final JavaMailSender mailSender;

    public void sendWeeklyAttendanceMail() {
        LocalDate weekStart = currentWeekMonday();
        LocalDate weekEnd = weekStart.plusDays(5);
        String periodHeading = formatDateRangeHeading(weekStart, weekEnd);

        List<Object[]> rows = attendanceRepository.getWeeklyAttendanceReport(weekStart, weekEnd);
        log.info("Preparing weekly attendance report email for {} with {} rows", periodHeading, rows.size());

        sendReportEmail(
                "Weekly Attendance Report (" + periodHeading + ")",
                buildWeeklyEmailBody(periodHeading),
                buildWeeklyAttachmentFileName(weekStart, weekEnd),
                AttendanceReportExcelBuilder.buildReport(
                        "Weekly Attendance",
                        "WEEKLY ATTENDANCE REPORT",
                        periodHeading,
                        rows
                )
        );

        log.info("Weekly attendance report email sent successfully for {}", periodHeading);
    }

    public void sendMonthlyAttendanceMail() {
        YearMonth currentMonth = YearMonth.from(LocalDate.now(IST));
        sendMonthlyAttendanceMail(currentMonth, attendanceRepository.getMonthlyAttendanceReport(
                currentMonth.atDay(1),
                currentMonth.atEndOfMonth()
        ));
    }

    private void sendMonthlyAttendanceMail(YearMonth month, List<Object[]> rows) {
        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();
        String periodHeading = formatDateRangeHeading(monthStart, monthEnd);
        String monthLabel = month.atDay(1).format(MONTH_YEAR);

        log.info("Preparing monthly attendance report email for {} with {} rows", monthLabel, rows.size());

        sendReportEmail(
                "Monthly Attendance Report - " + monthLabel,
                buildMonthlyEmailBody(monthLabel, periodHeading),
                buildMonthlyAttachmentFileName(month),
                AttendanceReportExcelBuilder.buildReport(
                        "Monthly Attendance",
                        "MONTHLY ATTENDANCE REPORT",
                        periodHeading + "  |  Working Days: Monday to Saturday",
                        rows
                )
        );

        log.info("Monthly attendance report email sent successfully for {}", monthLabel);
    }

    private void sendReportEmail(String subject, String body, String attachmentName, byte[] attachmentBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(REPORT_RECIPIENTS);
            helper.setSubject(subject);
            helper.setText(body, false);
            helper.addAttachment(
                    attachmentName,
                    new ByteArrayResource(attachmentBytes),
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );

            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send attendance report email: {}", subject, ex);
            throw new IllegalStateException("Failed to send attendance report email", ex);
        }
    }

    private LocalDate currentWeekMonday() {
        LocalDate today = LocalDate.now(IST);
        return today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private String formatDateRangeHeading(LocalDate start, LocalDate end) {
        return formatDateWithOrdinal(start) + " to " + formatDateWithOrdinal(end);
    }

    private String formatDateWithOrdinal(LocalDate date) {
        int day = date.getDayOfMonth();
        return date.format(MONTH_DAY) + " " + day + ordinalSuffix(day);
    }

    private String ordinalSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        return switch (day % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }

    private String buildWeeklyEmailBody(String periodHeading) {
        return """
                Dear Team,

                Please find attached the weekly attendance report for %s.

                The report includes employee names and the number of days present during the work week (Monday to Saturday).

                Regards,
                Attendance System
                """.formatted(periodHeading);
    }

    private String buildMonthlyEmailBody(String monthLabel, String periodHeading) {
        return """
                Dear Team,

                Please find attached the monthly attendance report for %s.

                Reporting period: %s
                Working calendar: Monday to Saturday (Sundays excluded)

                The attached Excel file summarizes days present for each employee during the completed month.

                Regards,
                Attendance System
                """.formatted(monthLabel, periodHeading);
    }

    private String buildWeeklyAttachmentFileName(LocalDate weekStart, LocalDate weekEnd) {
        return String.format(
                "weekly-attendance-report-%s-to-%s.xlsx",
                weekStart.format(DateTimeFormatter.ISO_LOCAL_DATE),
                weekEnd.format(DateTimeFormatter.ISO_LOCAL_DATE)
        );
    }

    private String buildMonthlyAttachmentFileName(YearMonth month) {
        return String.format("monthly-attendance-report-%s.xlsx", month);
    }
}
