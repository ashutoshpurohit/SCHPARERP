package com.myapp.handbook.domain;

public class TimeSlots {
    String StartTime;
    String EndTime;
    String SubjectName;
    String TeacherId;
    String TeacherName;

    public String getStartTime() {
        return StartTime;
    }

    public TimeSlots(String startTime, String endTime, String subjectName, String teacherId, String teacherName) {
        StartTime = startTime;
        EndTime = endTime;
        SubjectName = subjectName;
        TeacherId = teacherId;
        TeacherName = teacherName;
    }

    public void setStartTime(String startTime) {
        this.StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        this.EndTime = endTime;
    }

    public String getSubject() {
        return SubjectName;
    }

    public void setSubject(String subject) {
        this.SubjectName = subject;
    }

    public String getTeacherId() {
        return TeacherId;
    }

    public void setTeacherId(String teacherId) {
        this.TeacherId = teacherId;
    }

    public String getTeacherName() {
        return TeacherName;
    }

    public void setTeacherName(String teacherName) {
        this.TeacherName = teacherName;
    }

    public String getCurrentTimeSlot(){
        return getStartTime() + " - " + getEndTime();
    }
}
