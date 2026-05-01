package com.example.attendance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
public class UserAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "site_id")
    private Long siteId;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "check_in_lat")
    private Double checkInLat;

    @Column(name = "check_in_lng")
    private Double checkInLng;

    @Column(name = "check_out_lat")
    private Double checkOutLat;

    @Column(name = "check_out_lng")
    private Double checkOutLng;
}