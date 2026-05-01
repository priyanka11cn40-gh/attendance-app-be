package com.example.attendance.repository;

import com.example.attendance.entity.UserAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
            COALESCE(CAST(a.check_in_time AS VARCHAR), '-') AS checkIn,
            COALESCE(CAST(a.check_out_time AS VARCHAR), '-') AS checkOut
        FROM users u
        LEFT JOIN attendance a 
            ON u.id = a.user_id
            AND DATE(a.check_in_time) = CURRENT_DATE
        ORDER BY u.id
        """, nativeQuery = true)
    List<Object[]> getTodayAttendanceReport();
}
