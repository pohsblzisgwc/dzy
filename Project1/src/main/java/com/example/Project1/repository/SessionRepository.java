package com.example.Project1.repository;

import com.example.Project1.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Session findByToken(String token);
    void deleteByToken(String token);
}