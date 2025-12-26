package com.example.Project1.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ExamScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentId;
    private Long examId;
    private String subject;
    private Double score;
}