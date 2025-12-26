package com.example.Project1.repository;

import com.example.Project1.model.ExamScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamScoreRepository extends JpaRepository<ExamScore,Long> {
    public Optional<ExamScore> findByStudentIdAndExamId(String studentIdentifier, Long examId);
    public void deleteByExamId(Long examId);

}
