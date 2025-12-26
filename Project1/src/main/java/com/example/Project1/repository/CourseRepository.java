package com.example.Project1.repository;

import com.example.Project1.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByOrderByIdDesc();

    @Query(value = "SELECT c.* FROM course c " +
            "JOIN course_permission cp ON c.id = cp.course_id " +
            "WHERE cp.user_id = :userId", nativeQuery = true)
    List<Course> findCoursesByUserId(@Param("userId") Long userId);
}