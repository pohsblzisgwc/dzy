package com.example.Project1.repository;

import com.example.Project1.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByStudentIdentifier(String studentIdentifier);

    List<Student> findByCourseId(Long courseId);

    Integer countByCourseId(Long id);
    Optional<Student> findByStudentIdentifierAndCourseId(String studentIdentifier, Long courseId);
}