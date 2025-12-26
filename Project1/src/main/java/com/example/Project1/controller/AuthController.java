package com.example.Project1.controller;

import com.example.Project1.model.Session;
import com.example.Project1.model.User;
import com.example.Project1.repository.SessionRepository;
import com.example.Project1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    //1.1 用户登入
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            String token = UUID.randomUUID().toString().replace("-", "").substring(0, 15);
            Session session = new Session();
            session.setToken(token);
            session.setUserId(user.getId());
            session.setCreatedAt(LocalDateTime.now());
            sessionRepository.save(session);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("token", token);
            Map<String, Object> userMap = new LinkedHashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("role", user.getRole().name());
            userMap.put("name", user.getName());
            response.put("user", userMap);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(401).body(Map.of("message", "用户名或密码错误"));
    }

    //1.2 用户登出
    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String header) {
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            sessionRepository.deleteByToken(token);
        }
        return ResponseEntity.ok(Map.of("message", "已登出"));
    }

    //1.3 获取当前用户信息
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String header) {
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Session session = sessionRepository.findByToken(token);

            if (session != null) {
                Optional<User> userOpt = userRepository.findById(session.getUserId());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    Map<String, Object> userMap = new LinkedHashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("role", user.getRole().name());
                    userMap.put("name", user.getName());

                    return ResponseEntity.ok(Map.of("user", userMap));
                }
            }
        }
        return ResponseEntity.status(401).body(Map.of("message", "未登录或登录已过期"));
    }
}