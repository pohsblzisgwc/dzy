package com.example.Project1.service;

import com.example.Project1.model.Exam;
import com.example.Project1.model.ExamScore;
import com.example.Project1.model.Student;
import com.example.Project1.repository.CourseRepository;
import com.example.Project1.repository.ExamRepository;
import com.example.Project1.repository.ExamScoreRepository;
import com.example.Project1.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.*;

import java.util.*;

@Service
public class CourseScoreService {
    @Autowired
    private CourseRepository cr;
    @Autowired
    private StudentRepository sr;
    @Autowired
    private ExamRepository er;
    @Autowired
    private ExamScoreRepository esr;
    public Map<String,Object> getCourseScore(Long courseId){
        Map<String,Object> report = new HashMap<>();
        List<Map<String,Object>> exams = new ArrayList<>();
        List<Exam>examList=er.findByCourseId(courseId);
        examList.sort(Comparator.comparing(Exam::getId));
        for(Exam exam:examList){
            Map<String,Object> examMap = new HashMap<>();
            examMap.put("id",exam.getId());
            examMap.put("name",exam.getName());
            examMap.put("courseId",exam.getCourseId());
            examMap.put("weight",exam.getWeight());
            exams.add(examMap);
        }
        report.put("exams",exams);
        List<Student> studentList=sr.findByCourseId(courseId);
        List<Map<String,Object>> studentScores = new ArrayList<>();
        studentList.sort(Comparator.comparing(Student::getId));
        for(Student student:studentList){
            Map<String,Object> studentMap = new HashMap<>();
            studentMap.put("studentId",student.getId());
            studentMap.put("studentName",student.getName());
            studentMap.put("studentIdentifier",student.getStudentIdentifier());
            Map<String,Double>studentScore = new HashMap<>();
            double total=0;
            for(Exam exam:examList){
                Optional<ExamScore> examScores=esr.findByStudentIdAndExamId(student.getStudentIdentifier(),exam.getId());
                if(examScores.isEmpty()){
                    return null;
                }
                studentScore.put(String.valueOf(exam.getId()),examScores.get().getScore());
                total+=examScores.get().getScore()*exam.getWeight();
            }
            studentMap.put("scores",studentScore);
            studentMap.put("total",total);
            studentScores.add(studentMap);
        }
        report.put("students",studentScores);
        return report;
    }
    public boolean uploadCourseGrades(Workbook workbook,Long courseId,Long examId){
        Sheet sheet = workbook.getSheetAt(0);
        int idIndex = 2;
        Row SecondRow=sheet.getRow(1);
        if(SecondRow==null){
            System.out.println("错误");
            return false;
        }
        if(SecondRow.getCell(2)==null){
            return false;
        }
        for(int i=1;i<=sheet.getLastRowNum();i++){
            System.out.println(i);
            try {
                Row nextRow = sheet.getRow(i);
                String studentIdentifier="";
                studentIdentifier = nextRow.getCell(0).getStringCellValue();
                Double grade = nextRow.getCell(idIndex).getNumericCellValue();
                String studentName=nextRow.getCell(1).getStringCellValue();
                ExamScore examScore = new ExamScore();
                examScore.setExamId(examId);
                examScore.setStudentId(studentIdentifier);
                examScore.setScore(grade);
                Optional<ExamScore> existExamScore=esr.findByStudentIdAndExamId(studentIdentifier,examId);
                if(existExamScore.isEmpty()){
                    esr.save(examScore);
                }else{
                    examScore.setId(existExamScore.get().getId());
                }
                Optional<Student> exsitStudent=sr.findByStudentIdentifierAndCourseId(studentIdentifier,courseId);
                if(exsitStudent.isEmpty()){
                    Student student=new Student();
                    student.setStudentIdentifier(studentIdentifier);
                    student.setCourseId(courseId);
                    student.setName(studentName);
                    sr.save(student);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }
    public Integer exportCourseGrade(Workbook workbook, Long courseId){
        Sheet sheet=workbook.createSheet();
        int rowIndex=0;
        Row rowHeader =sheet.createRow(rowIndex);
        rowHeader.createCell(0).setCellValue("Student ID");
        rowHeader.createCell(1).setCellValue("Name");
        List<Exam> examList=er.findByCourseId(courseId);
        examList.sort(Comparator.comparing(Exam::getId));
        for(int i=0;i<examList.size();i++){
            rowHeader.createCell(i+2).setCellValue(examList.get(i).getName());
        }
        rowHeader.createCell(examList.size()+2).setCellValue("Weighted Total");
        List<Student> studentList=sr.findByCourseId(courseId);
        for(Student student:studentList){
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(student.getId());
            row.createCell(1).setCellValue(student.getName());
            double weightedTotal=0;
            for(int j=0;j<examList.size();j++){
                try {
                    ExamScore examScore = esr.findByStudentIdAndExamId(student.getStudentIdentifier(),examList.get(j).getId()).get();
                    row.createCell(j+2).setCellValue(examScore.getScore());
                    weightedTotal+=examScore.getScore()*examList.get(j).getWeight();
                }catch (Exception e){
                    System.out.println(e.getMessage());
                    return -2;
                }
            }
            row.createCell(examList.size()+2).setCellValue(weightedTotal);
        }
        return 1;
    }
}
