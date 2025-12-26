package com.example.Project1.controller.report;

import com.example.Project1.model.Student;
import com.example.Project1.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.apache.poi.xssf.usermodel.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
public class StudentScoreExport {
    @Autowired
    private ReportService rs;
    @GetMapping("/api/students/{studentIdentifier}/report/export")
    public void studentScoreExport(HttpServletResponse response, @PathVariable String studentIdentifier) throws IOException {
        Optional<Workbook> res = rs.studentScoreExport(studentIdentifier);
        if (res.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try (Workbook workbook = res.get()) {

            String fileName = URLEncoder.encode("课程成绩单.xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
            workbook.write(response.getOutputStream());
        }
    }
}
