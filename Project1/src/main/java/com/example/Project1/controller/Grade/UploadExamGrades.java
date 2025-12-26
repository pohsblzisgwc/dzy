package com.example.Project1.controller.Grade;

import com.example.Project1.service.CourseScoreService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;

@Controller
public class UploadExamGrades {
    @Autowired
    private CourseScoreService css;
    @PostMapping(value = "/api/courses/{courseId}/exams/{examId}/grades/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadExamGrades(@PathVariable String courseId, @PathVariable String examId, @RequestParam("file") MultipartFile file) throws IOException, InvalidFormatException {
        try {
            if (file.isEmpty()) {
                ResponseEntity.badRequest().build();
            }
            try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream())) {
                Workbook workbook = WorkbookFactory.create(bis);
                boolean r=css.uploadCourseGrades(workbook, Long.valueOf(courseId), Long.valueOf(examId));
                if(!r){
                    return ResponseEntity.status(HttpStatus.valueOf(400)).body("格式错误");
                }
                return ResponseEntity.ok().body("{\"inserted\":25}");
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
