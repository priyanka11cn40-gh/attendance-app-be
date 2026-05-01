package com.example.attendance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sites")
@Getter
@Setter
public class Sites {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long siteId;
    private String name;
    private double latitude;
    private double longitude;

    @Column(name = "radius_meters")
    private double radiusMeters;
}
