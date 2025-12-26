package com.example.Project1.controller.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.Project1.model.Student;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import com.example.Project1.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetStudentScoreReport {
    @Autowired
    private ReportService rs;
    @GetMapping("/api/students/{id}/report")
    public ResponseEntity<String> getStudentReport(@PathVariable String id) {
        String s = "";
        try{
             s= new ObjectMapper().writeValueAsString(rs.getStudentScoreReport(id).get());
        }catch (Exception e){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(s);
    }
}
