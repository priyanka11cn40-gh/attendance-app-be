package com.example.attendance.repository;

import com.example.attendance.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {

    Optional<Users> findByUserId(String userId);
}