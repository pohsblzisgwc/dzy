package com.example.Project1.controller.Grade;

import com.example.Project1.service.CourseScoreService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class CourseGradesExport {
    @Autowired
    private CourseScoreService css;
    @GetMapping("/api/courses/{courseId}/grades/export")
    public void courseGradeExport(HttpServletResponse response, @PathVariable("courseId") Long courseId) throws IOException {
        Workbook wb=new HSSFWorkbook();
        int res=css.exportCourseGrade(wb,courseId);
        System.out.println(res);
        try {
            if (res == 1) {
                String fileName = URLEncoder.encode("课程成绩导出.xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
                wb.write(response.getOutputStream());
            } else {
                response.setStatus(400);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            response.setStatus(400);
        }
    }
}
