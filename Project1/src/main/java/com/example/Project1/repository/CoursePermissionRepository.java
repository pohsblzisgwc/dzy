package com.example.Project1.repository;

import com.example.Project1.model.CoursePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CoursePermissionRepository extends JpaRepository<CoursePermission, CoursePermission.CoursePermissionId> {
    List<CoursePermission> findByUserId(Long userId);
    List<CoursePermission> findByCourseId(Long courseId);
    void deleteByCourseId(Long courseId);
}