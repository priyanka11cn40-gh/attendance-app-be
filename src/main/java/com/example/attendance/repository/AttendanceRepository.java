package com.example.attendance.repository;

import com.example.attendance.entity.UserAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<UserAttendance, Long> {

    @Query(value = """
        SELECT 
            u.id,
            u.name,
            CASE 
                WHEN a.id IS NOT NULL THEN 'Present'
                ELSE 'Absent'
            END AS status,
            COALESCE(TO_CHAR(a.check_in_time + INTERVAL '5 hours 30 minutes', 'YYYY-MM-DD HH24:MI:SS'), '-') AS checkIn,
            COALESCE(TO_CHAR(a.check_out_time + INTERVAL '5 hours 30 minutes', 'YYYY-MM-DD HH24:MI:SS'), '-') AS checkOut
        FROM users u
        LEFT JOIN attendance a 
            ON u.id = a.user_id
            AND DATE(a.check_in_time + INTERVAL '5 hours 30 minutes') = DATE(CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Kolkata')
        ORDER BY u.id
        """, nativeQuery = true)
    List<Object[]> getTodayAttendanceReport();

    @Query(value = """
        SELECT
            u.name,
            COUNT(DISTINCT DATE(a.check_in_time + INTERVAL '5 hours 30 minutes')) AS days_present
        FROM users u
        LEFT JOIN attendance a
            ON u.id = a.user_id
            AND DATE(a.check_in_time + INTERVAL '5 hours 30 minutes') >= CAST(:weekStart AS DATE)
            AND DATE(a.check_in_time + INTERVAL '5 hours 30 minutes') <= CAST(:weekEnd AS DATE)
        GROUP BY u.id, u.name
        ORDER BY u.name
        """, nativeQuery = true)
    List<Object[]> getWeeklyAttendanceReport(
            @Param("weekStart") LocalDate weekStart,
            @Param("weekEnd") LocalDate weekEnd
    );

    @Query(value = """
        SELECT
            u.name,
            COUNT(DISTINCT DATE(a.check_in_time + INTERVAL '5 hours 30 minutes')) AS days_present
        FROM users u
        LEFT JOIN attendance a
            ON u.id = a.user_id
            AND DATE(a.check_in_time + INTERVAL '5 hours 30 minutes') >= CAST(:monthStart AS DATE)
            AND DATE(a.check_in_time + INTERVAL '5 hours 30 minutes') <= CAST(:monthEnd AS DATE)
            AND EXTRACT(DOW FROM DATE(a.check_in_time + INTERVAL '5 hours 30 minutes')) <> 0
        GROUP BY u.id, u.name
        ORDER BY u.name
        """, nativeQuery = true)
    List<Object[]> getMonthlyAttendanceReport(
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd
    );
}
