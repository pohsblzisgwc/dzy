package com.example.Project1.service.temp;

import com.example.Project1.model.Student;
public class StudentCourseScore {
    private String studentId;
    private double CourseScore;
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public Double getCourseScore() {
        return CourseScore;
    }
    public void setCourseScore(Double courseScore) {
        this.CourseScore = courseScore;
    }
}
