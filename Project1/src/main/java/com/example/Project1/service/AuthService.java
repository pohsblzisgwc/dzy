package com.example.Project1.service;

import com.example.Project1.model.User;
import com.example.Project1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    //1.3 获取当前登录用户
    public User getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(username);
    }

    //判断是否为管理员
    public boolean isAdmin() {
        User user = getCurrentUser();
        return user != null && user.getRole() == User.Role.admin;
    }

    //判断是否有权访问某课程
    public boolean canAccessCourse(Long courseId) {
        if (isAdmin()) return true;
        User user = getCurrentUser();
        return userRepository.hasPermission(user.getId(), courseId);
    }
}