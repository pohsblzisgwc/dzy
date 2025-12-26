package com.example.Project1.controller;

import com.example.Project1.model.Course;
import com.example.Project1.model.CoursePermission;
import com.example.Project1.model.User;
import com.example.Project1.repository.CoursePermissionRepository;
import com.example.Project1.repository.CourseRepository;
import com.example.Project1.repository.ExamRepository;
import com.example.Project1.repository.UserRepository;
import com.example.Project1.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private CoursePermissionRepository coursePermissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    // 3.1 获取课程列表
    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        List<Course> courses;
        if (authService.isAdmin()) {
            courses = courseRepository.findAll();
        } else {
            Long userId = authService.getCurrentUser().getId();
            courses = courseRepository.findCoursesByUserId(userId);
        }
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Course c : courses) {
            Map<String, Object> courseMap = new LinkedHashMap<>();
            courseMap.put("id", c.getId());
            courseMap.put("name", c.getName());
            courseMap.put("code", c.getCode());
            responseList.add(courseMap);
        }

        return ResponseEntity.ok(responseList);
    }

    // 3.2 创建课程
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        if (!authService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "无权限创建课程"));
        }
        if (course.getName() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Course name is required"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(courseRepository.save(course));
    }

    // 3.3 删除课程
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        if (!authService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "无权限删除课程"));
        }

        if (!courseRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "课程不存在"));
        }

        examRepository.deleteByCourseId(id);
        coursePermissionRepository.deleteByCourseId(id);
        courseRepository.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "课程已删除"));
    }


    // 3.4 获取课程权限列表
    @GetMapping("/{id}/permissions")
    public ResponseEntity<?> getCoursePermissions(@PathVariable Long id) {
        if (!authService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "无权查看权限列表"));
        }

        List<CoursePermission> permissions = coursePermissionRepository.findByCourseId(id);
        List<Map<String, Object>> teacherList = new ArrayList<>();

        for (CoursePermission cp : permissions) {
            userRepository.findById(cp.getUserId()).ifPresent(user -> {
                Map<String, Object> teacherInfo = new LinkedHashMap<>();
                teacherInfo.put("userId", user.getId());
                teacherInfo.put("username", user.getUsername());
                teacherInfo.put("name", user.getName());
                teacherList.add(teacherInfo);
            });
        }
        if(teacherList.isEmpty()){
            return ResponseEntity.badRequest().body("[]");
        }
        return ResponseEntity.ok(teacherList);
    }

    // 3.5 分配课程权限
    @PostMapping("/{id}/permissions")
    public ResponseEntity<?> assignPermission(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        if (!authService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "无权分配权限"));
        }
        Long userId = request.get("userId");
        if (userId == null) {
            return ResponseEntity.status(400).body(Map.of("message", "userId is required"));
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.teacher) {
            return ResponseEntity.status(400).body(Map.of("message", "用户不存在或不是教师"));
        }
        CoursePermission.CoursePermissionId permissionId = new CoursePermission.CoursePermissionId();
        permissionId.setCourseId(id);
        permissionId.setUserId(userId);
        if (coursePermissionRepository.existsById(permissionId)) {
            return ResponseEntity.status(400).body(Map.of("message", "该教师已有此课程权限"));
        }
        CoursePermission permission = new CoursePermission();
        permission.setCourseId(id);
        permission.setUserId(userId);
        coursePermissionRepository.save(permission);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "权限已分配"));
    }

    //3.6 移除课程权限
    @DeleteMapping("/{id}/permissions/{userId}")
    @Transactional
    public ResponseEntity<?> removePermission(@PathVariable Long id, @PathVariable Long userId) {
        if (!authService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "仅管理员可移除权限"));
        }

        CoursePermission.CoursePermissionId permissionId = new CoursePermission.CoursePermissionId();
        permissionId.setCourseId(id);
        permissionId.setUserId(userId);

        if (!coursePermissionRepository.existsById(permissionId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "权限记录不存在"));
        }

        coursePermissionRepository.deleteById(permissionId);
        return ResponseEntity.ok(Map.of("message", "权限已移除"));
    }
}