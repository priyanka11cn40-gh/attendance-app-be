package com.example.attendance.repository;

import com.example.attendance.entity.UserAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAttendanceRepository extends JpaRepository<UserAttendance, String> {

    Optional<UserAttendance> findByUserIdAndCheckOutTimeIsNull(String userId);
}