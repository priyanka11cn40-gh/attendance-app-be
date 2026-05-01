package com.example.attendance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class Users {
    @Id
    @Column(name = "id")
    private String userId;

    private String name;
    private String phoneNumber;
    private String password;
    private Boolean active;
}
