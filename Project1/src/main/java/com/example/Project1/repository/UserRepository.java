package com.example.Project1.repository;

import com.example.Project1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Optional<User> findById(Long id);

    @Query(value = "SELECT COUNT(*) > 0 FROM course_permission WHERE user_id = :userId AND course_id = :courseId", nativeQuery = true)
    boolean hasPermission(@Param("userId") Long userId, @Param("courseId") Long courseId);
}