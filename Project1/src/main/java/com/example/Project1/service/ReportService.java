package com.example.Project1.service;

import com.example.Project1.model.Course;
import com.example.Project1.model.Exam;
import com.example.Project1.model.ExamScore;
import com.example.Project1.model.Student;
import com.example.Project1.repository.CourseRepository;
import com.example.Project1.repository.ExamRepository;
import com.example.Project1.repository.ExamScoreRepository;
import com.example.Project1.repository.StudentRepository;
import com.example.Project1.service.temp.StudentCourseScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.util.*;


@Service
public class ReportService {
    @Autowired
    private StudentRepository sr;
    @Autowired
    private ExamScoreRepository esr;
    @Autowired
    private CourseRepository cr;
    @Autowired
    private ExamRepository er;
    @Autowired
    @Lazy
    private ReportService rs;
    public List<Student> getStudentsList() {
        return sr.findAll();
    }
    public Optional<Map<String,Object>> getStudentScoreReport(String studentIdentifier) {
        double overallTotal=0.0;
        int coursesWithScores=0;
        int totalCourses=0;
        List<Student> courses=sr.findByStudentIdentifier(studentIdentifier);
        if(courses.isEmpty()){
            return Optional.empty();
        }
        Map<String,Object> report= new HashMap<>();
        report.put("studentIdentifier",studentIdentifier);
        report.put("studentName",(courses.get(0)).getName());
        List<Map<String,Object>> courseReports=new ArrayList<>();
        courses.sort(Comparator.comparing(Student::getCourseId));
        for(Student i:courses){
            totalCourses++;
            Map<String,Object> examReports=new HashMap<>();
            double totalScore=0.0;
            int ranking=0;
            int totalStudents=0;
            Map<String,Object> courseReport=new HashMap<>();
            courseReport.put("courseId",i.getCourseId());
            Optional<Course> tarCourse=cr.findById(i.getCourseId());
            if(!tarCourse.isPresent()){
                return Optional.empty();
            }
            courseReport.put("courseName",tarCourse.get().getName());
            courseReport.put("courseCode",tarCourse.get().getCode());
            List<Exam> examsOfCourse=er.findByCourseId(i.getCourseId());
            if(examsOfCourse.isEmpty()){
                courseReport.put("totalStudents",sr.countByCourseId(tarCourse.get().getId()));
                courseReports.add(courseReport);
                continue;
            }
            else{

                for(Exam tarExam:examsOfCourse) {

                    Optional<ExamScore> tarExamScore = esr.findByStudentIdAndExamId(studentIdentifier, tarExam.getId());
                    if (!tarExamScore.isPresent()) {
                        examReports.put(tarExam.getName(), 0);
                    } else {
                        examReports.put(tarExam.getName(), tarExamScore.get().getScore());
                        totalScore += tarExamScore.get().getScore() * tarExam.getWeight();
                    }
                }
            }
            courseReport.put("examScores",examReports);
            courseReport.put("total",totalScore);
            List<Student> studentsWithTarCourse=sr.findByCourseId(i.getCourseId());
            List<StudentCourseScore> studentCourseScores=new ArrayList<>();
            for(Student student:studentsWithTarCourse){
                totalStudents++;
                StudentCourseScore studentCourseScore=new StudentCourseScore();
                studentCourseScore.setStudentId(student.getStudentIdentifier());
                studentCourseScore.setCourseScore(0.0);
                List<Exam> Exams=er.findByCourseId(student.getCourseId());
                for(Exam exam:Exams){
                    Optional<ExamScore> examScore=esr.findByStudentIdAndExamId(student.getStudentIdentifier(), exam.getId());
                    studentCourseScore.setCourseScore(examScore.get().getScore()*exam.getWeight()+studentCourseScore.getCourseScore());
                }
                studentCourseScores.add(studentCourseScore);
            }
            studentCourseScores.sort(Comparator.comparing(StudentCourseScore::getCourseScore).reversed());
            for(int j=0;j<studentCourseScores.size();j++){
                if(studentCourseScores.get(j).getStudentId().equals(studentIdentifier)){
                    ranking=j+1;
                    break;
                }
            }
            courseReport.put("ranking",ranking);
            courseReport.put("totalStudents",totalStudents);
            overallTotal+=totalScore;
            coursesWithScores++;
            courseReports.add(courseReport);
        }
        report.put("courseReports",courseReports);
        report.put("overallTotal",overallTotal);
        report.put("coursesWithScores",coursesWithScores);
        report.put("totalCourses",totalCourses);
        return Optional.of(report);

    }
    public Optional<Workbook> studentScoreExport(String studentIdentifier) throws IOException {
        try {
            Workbook workbook = new XSSFWorkbook();
            int rowIndex=0;
            Sheet sheet = workbook.createSheet("成绩单");
            sheet.setColumnWidth(0, 4000);

            Optional<Map<String,Object>> res=rs.getStudentScoreReport(studentIdentifier);
            if(res.isEmpty()){
                return Optional.empty();
            }
            List<Map<String,Object>> courseScores=(List)(res.get().get("courseReports"));
            for(int i=0;i<courseScores.size();i++){
                Map<String,Object> courseScore=courseScores.get(i);
                Row rowHeader=sheet.createRow(rowIndex++);
                Row rowBody=sheet.createRow(rowIndex++);
                rowIndex++;
                int lineIndex=0;
                rowHeader.createCell(lineIndex).setCellValue("课程名称");
                rowBody.createCell(lineIndex++).setCellValue((String)courseScore.get("courseName"));
                rowHeader.createCell(lineIndex).setCellValue("课程代码");
                rowBody.createCell(lineIndex++).setCellValue((String)courseScore.get("courseCode"));
                Map<String,Object> examScores=(Map)(courseScore.get("examScores"));
                for(Map.Entry<String,Object> entry:examScores.entrySet()){
                    rowHeader.createCell(lineIndex).setCellValue(entry.getKey());
                    rowBody.createCell(lineIndex++).setCellValue((Double) entry.getValue());
                }
                rowHeader.createCell(lineIndex).setCellValue("总成绩");
                rowBody.createCell(lineIndex++).setCellValue((Double) courseScore.get("total"));
                rowHeader.createCell(lineIndex).setCellValue("排名");
                rowBody.createCell(lineIndex++).setCellValue(String.valueOf(courseScore.get("ranking")));
                rowHeader.createCell(lineIndex).setCellValue("该科目学生总数");
                rowBody.createCell(lineIndex++).setCellValue(String.valueOf(courseScore.get("totalStudents")));
            }
            Row rowCoursesWithScore=sheet.createRow(rowIndex++);
            Row rowTotalCourses=sheet.createRow(rowIndex++);
            rowCoursesWithScore.createCell(0).setCellValue("有成绩的科目数");
            rowCoursesWithScore.createCell(1).setCellValue(String.valueOf(res.get().get("coursesWithScores")));
            rowTotalCourses.createCell(0).setCellValue("总科目数");
            rowTotalCourses.createCell(1).setCellValue(String.valueOf(res.get().get("totalCourses")));
            return Optional.of(workbook);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
