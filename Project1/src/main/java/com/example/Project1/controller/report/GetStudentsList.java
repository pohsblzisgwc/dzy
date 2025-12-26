package com.example.Project1.controller.report;

import com.example.Project1.model.Student;
import com.example.Project1.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class GetStudentsList {
    @Autowired
    private ReportService rs;
    @GetMapping("/api/students")
    public List<Student> getStudentsList() {
        return rs.getStudentsList();
    }

}
