package com.example.Project1.controller.Exam;

import com.example.Project1.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class deleteExam {
    @Autowired
    private ExamService es;
    @DeleteMapping("/api/courses/{courseId}/exams/{examId}")
    public ResponseEntity<?> deleteExam(@PathVariable Long courseId, @PathVariable Long examId){
        int res=es.deleteExam(courseId,examId);
        if(res==1){
            return ResponseEntity.ok().body("{\"message\": \"考试已删除\"}");
        }
        return ResponseEntity.notFound().build();
    }
}
