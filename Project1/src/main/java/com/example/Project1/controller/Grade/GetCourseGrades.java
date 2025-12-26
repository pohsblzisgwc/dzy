package com.example.Project1.controller.Grade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.Project1.service.CourseScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class GetCourseGrades {
    @Autowired
    private CourseScoreService css;
    @GetMapping("/api/courses/{id}/grades")
    public ResponseEntity<String> getCourseGrades(@PathVariable String id) throws JsonProcessingException {
        Map<String,Object> res=css.getCourseScore(Long.valueOf(id));
        if(res==null){
            return ResponseEntity.badRequest().body("[]");
        }
        else{
            try {
                return new ResponseEntity<>(new ObjectMapper().writeValueAsString(res), HttpStatus.OK);
            }catch(Exception e){
                return ResponseEntity.badRequest().body("[]");
            }
        }
    }
}
