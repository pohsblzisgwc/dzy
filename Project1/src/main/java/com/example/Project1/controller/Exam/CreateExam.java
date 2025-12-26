package com.example.Project1.controller.Exam;


import com.example.Project1.model.Exam;
import com.example.Project1.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class CreateExam {
    @Autowired
    private ExamService es;
    @PostMapping("/api/courses/{courseId}/exams")
    public ResponseEntity<?> createExam(@PathVariable Long courseId, @RequestBody Exam exam){
        Optional<Exam> examNew=es.createExam(courseId,exam);
        if(examNew.isPresent()){
            return ResponseEntity.ok().body(examNew.get());
        }else{
            return ResponseEntity.badRequest().build();
        }
    }
}
