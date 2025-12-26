package com.example.Project1.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "session")
@Data
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String token;

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
}