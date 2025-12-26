package com.example.Project1.service;

import com.example.Project1.model.Exam;
import com.example.Project1.repository.CourseRepository;
import com.example.Project1.repository.ExamRepository;
import com.example.Project1.repository.ExamScoreRepository;
import com.example.Project1.repository.StudentRepository;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExamService {
    @Autowired
    private StudentRepository sr;
    @Autowired
    private CourseRepository cr;
    @Autowired
    private ExamRepository er;
    @Autowired
    private ExamScoreRepository esr;
    public List<Map<String,Object>> GetExams(Long courseId){
        try {
            List<Exam> examList = er.findByCourseId(courseId);
            List<Map<String, Object>> examReports = new ArrayList<>();
            for (Exam exam : examList) {
                Map<String, Object> examReport = new HashMap<>();
                examReport.put("id", exam.getId());
                examReport.put("name", exam.getName());
                examReport.put("courseId", exam.getCourseId());
                examReport.put("weight", exam.getWeight());
                examReports.add(examReport);
            }
            return examReports;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public Optional<Exam> createExam(Long courseId, Exam exam){
        if(exam==null||exam.getWeight()==null||exam.getName()==null){
            return Optional.empty();
        }
        if(exam.getId()!=null){
            return Optional.empty();
        }
        exam.setCourseId(courseId);
        return Optional.of(er.save(exam));
    }
    public int deleteExam(Long courseId, Long examId){
        Optional<Exam> exam = er.findById(examId);
        if(exam.isEmpty()){
            return -1;
        }
        er.deleteById(examId);
        esr.deleteByExamId(examId);
        return 1;
    }
}
