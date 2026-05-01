package com.example.attendance.service;

import com.example.attendance.repository.AttendanceRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceMailService {

    private final AttendanceRepository attendanceRepository;
    private final JavaMailSender mailSender;

    public void sendDailyAttendanceMail() {

        List<Object[]> rows = attendanceRepository.getTodayAttendanceReport();

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("nsvsrikar@gmail.com");
            helper.setSubject("Daily Attendance Report");
            helper.setText("Please find the attached daily attendance report.", false);
            helper.addAttachment(
                    "attendance-report.csv",
                    new ByteArrayResource(buildCsvReport(rows).getBytes(StandardCharsets.UTF_8)),
                    "text/csv"
            );

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildCsvReport(List<Object[]> rows) {
        StringBuilder csv = new StringBuilder();
        csv.append("Emp ID,Name,Status,Check In,Check Out\n");

        for (Object[] row : rows) {
            csv.append(csvValue(row[0])).append(",")
                    .append(csvValue(row[1])).append(",")
                    .append(csvValue(row[2])).append(",")
                    .append(csvValue(row[3])).append(",")
                    .append(csvValue(row[4])).append("\n");
        }

        return csv.toString();
    }

    private String csvValue(Object value) {
        String text = value == null ? "" : value.toString();
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
