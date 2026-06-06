package com.example.attendance.repository;

import com.example.attendance.entity.UserAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserAttendanceRepository extends JpaRepository<UserAttendance, Long> {

    Optional<UserAttendance> findByUserIdAndCheckOutTimeIsNull(String userId);

    Optional<UserAttendance> findTopByUserIdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(String userId);

    @Query(value = """
        SELECT
            a.id,
            u.id AS emp_id,
            u.name,
            u.email
        FROM attendance a
        INNER JOIN users u ON u.id = a.user_id
        WHERE a.check_out_time IS NULL
        AND DATE(a.check_in_time + INTERVAL '5 hours 30 minutes') = CAST(:attendanceDate AS DATE)
        ORDER BY u.id
        """, nativeQuery = true)
    List<Object[]> findMissedCheckoutsForDate(@Param("attendanceDate") LocalDate attendanceDate);
}
