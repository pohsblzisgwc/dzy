package com.example.Project1.controller;

import com.example.Project1.model.User;
import com.example.Project1.repository.UserRepository;
import com.example.Project1.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    //2.1 获取用户列表
    @GetMapping
    public ResponseEntity<?> listUsers() {
        if (!authService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "无权限访问用户列表"));
        }

        List<User> users = userRepository.findAll();
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(users);
    }

    //2.2 创建用户
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (!authService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "无权限创建用户"));
        }

        if (user.getUsername() == null || user.getPassword() == null || user.getRole() == null) {
//        if (user.getUsername() == null || user.getRole() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "缺少必要字段"));
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "用户名已存在"));
        }
        if (user.getRole() != User.Role.admin && user.getRole() != User.Role.teacher) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "角色必须是teacher或admin"));
        }
        if (user.getName() == null) {
            user.setName(user.getUsername());
        }

        User savedUser = userRepository.save(user);
        savedUser.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    //2.3 删除用户
    @DeleteMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        if (!authService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "无权限删除用户"));
        }

        User currentUser = authService.getCurrentUser();
        if (currentUser != null && currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "不能删除自己"));
        }

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        userRepository.deleteById(userId);
        return ResponseEntity.ok(Map.of("message", "用户已删除"));
    }

    //2.4 修改用户密码
    @PostMapping("/{userId}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        User currentUser = authService.getCurrentUser();

        boolean canEdit = authService.isAdmin();
        if (!canEdit) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "没有权限修改此用户密码"));
        }

        if (newPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "newPassword is required"));
        }

        Optional<User> targetUserOpt = userRepository.findById(userId);
        if (targetUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }
        User targetUser = targetUserOpt.get();

        targetUser.setPassword(newPassword);
        userRepository.save(targetUser);

        return ResponseEntity.ok(Map.of("message", "密码已更新"));
    }
}