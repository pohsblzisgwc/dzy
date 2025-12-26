package com.example.Project1.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@IdClass(CoursePermission.CoursePermissionId.class)
public class CoursePermission {

    @Id
    @Column(nullable = false)
    private Long userId;

    @Id
    @Column(nullable = false)
    private Long courseId;

    @Data
    public static class CoursePermissionId implements Serializable {
        private Long userId;
        private Long courseId;
    }
}