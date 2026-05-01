package com.example.attendance.repository;

import com.example.attendance.entity.Sites;
import com.example.attendance.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteRepository extends JpaRepository<Sites, Long> {
    Optional<Sites> findBySiteId(Long siteId);
}