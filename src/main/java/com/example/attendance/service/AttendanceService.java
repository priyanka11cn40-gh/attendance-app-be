package com.example.attendance.service;

import com.example.attendance.entity.Sites;
import com.example.attendance.entity.UserAttendance;
import com.example.attendance.repository.SiteRepository;
import com.example.attendance.repository.UserAttendanceRepository;
import com.example.attendance.util.LocationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final SiteRepository siteRepository;
    private final UserAttendanceRepository attendanceRepository;
    private final LocationUtil locationUtil;

    public String checkIn(String userId, double lat, double lng) {
        log.info("Check-in request | userId={} lat={} lng={}", userId, lat, lng);

        Optional<UserAttendance> existing =
                attendanceRepository.findByUserIdAndCheckOutTimeIsNull(userId);

        if (existing.isPresent()) {
            log.warn("User already checked in: {}", userId);
            return "Already checked in!";
        }

        List<Sites> sites = siteRepository.findAll();

        for (Sites site : sites) {

            double distance = locationUtil.distanceInMeters(
                    lat, lng,
                    site.getLatitude(), site.getLongitude()
            );

            log.info("Checking site={} distance={} meters", site.getName(), distance);

            if (distance <= site.getRadiusMeters()) {

                UserAttendance attendance = new UserAttendance();
                attendance.setUserId(userId);
                attendance.setSiteId(site.getSiteId());
                attendance.setCheckInTime(LocalDateTime.now());
                attendance.setCheckInLat(lat);
                attendance.setCheckInLng(lng);

                attendanceRepository.save(attendance);

                log.info("Check-in SUCCESS for user={} at site={}", userId, site.getName());
                return "Check-in SUCCESS at site: " + site.getName();
            }
        }
        log.warn("Check-in FAILED for user={} - outside all sites", userId);
        return "Check-in FAILED: Not in valid location";
    }

    public String checkOut(String userId, double lat, double lng) {
        log.info("userId:{}",userId);
        log.info("Check-out request | userId={} lat={} lng={}", userId, lat, lng);
        UserAttendance attendance = attendanceRepository
                .findByUserIdAndCheckOutTimeIsNull(userId)
                .orElseThrow(() -> new RuntimeException("No active check-in found"));

        Sites site = siteRepository.findBySiteId(attendance.getSiteId())
                .orElseThrow(() -> new RuntimeException("Site not found"));

        double distance = locationUtil.distanceInMeters(
                lat, lng,
                site.getLatitude(), site.getLongitude()
        );

        log.info("Check-out distance={} for site={}", distance, site.getName());
        if (distance > site.getRadiusMeters()) {
            log.warn("Check-out FAILED for user={} - outside site", userId);
            return "Check-out FAILED: Not in site location";
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        attendance.setCheckOutLat(lat);
        attendance.setCheckOutLng(lng);

        attendanceRepository.save(attendance);
        log.info("Check-out SUCCESS for user={}", userId);
        return "Check-out SUCCESS";
    }
}