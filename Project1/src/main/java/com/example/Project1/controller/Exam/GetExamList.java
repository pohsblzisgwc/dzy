package com.example.Project1.controller.Exam;


import com.example.Project1.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class GetExamList {
    @Autowired
    private ExamService es;
    @GetMapping("/api/courses/{courseId}/exams")
    public ResponseEntity<String> getExams(@PathVariable Long courseId) {
        List<Map<String, Object>> examReports =es.GetExams(courseId);
        if (examReports.isEmpty()) {
            try {
                return ResponseEntity.badRequest().body(new ObjectMapper().writeValueAsString(examReports));/*.body("{ \"message\": \"Course not found\" }");*/
            }catch (Exception e){
                System.out.println(e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        }
        else{
            try{
                return ResponseEntity.ok().body(new ObjectMapper().writeValueAsString(examReports));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return ResponseEntity.badRequest().build();/*.body("{ \"message\": \"Course not found\" }");*/
            }
        }
    }
}
