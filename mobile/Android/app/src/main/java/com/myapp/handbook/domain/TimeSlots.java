package com.myapp.handbook.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeSlots {
    String StartTime;
    String EndTime;
    String SubjectName;
    String TeacherId;
    String TeacherName;

    String ClassStandard;

    public String getTeacherClassStd() {
        return ClassStandard;
    }

    public void setTeacherClassStd(String teacherClassStd) {
        this.ClassStandard = teacherClassStd;
    }

    public String getStartTime()  {
        String tempStartTime = StartTime;
        DateFormat inputFormat = new SimpleDateFormat("kk:mm");
        DateFormat outputFormat = new SimpleDateFormat("kk:mm");
        try {
            StartTime = outputFormat.format(inputFormat.parse(tempStartTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return StartTime;
    }

    public TimeSlots(String startTime, String endTime, String subjectName, String teacherId, String teacherName) {
        StartTime = startTime;
        EndTime = endTime;
        SubjectName = subjectName;
        TeacherId = teacherId;
        TeacherName = teacherName;
    }

    public TimeSlots(String startTime, String endTime, String subjectName, String teacherClassStd) {

        StartTime = startTime;
        EndTime= endTime;
        SubjectName = subjectName;
        this.ClassStandard = teacherClassStd;
    }

    public void setStartTime(String startTime) {
        this.StartTime = startTime;
    }

    public String getEndTime() {
        String tempEndTime = EndTime;
        DateFormat inputFormat = new SimpleDateFormat("kk:mm");
        DateFormat outputFormat = new SimpleDateFormat("kk:mm");
        try {
            EndTime= outputFormat.format(inputFormat.parse(tempEndTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        if(TeacherName!=null)
            return TeacherName;
        else
            return " ";
    }

    public void setTeacherName(String teacherName) {
        this.TeacherName = teacherName;
    }

    public String getCurrentTimeSlot(){

        return getStartTime() + " - " + getEndTime();

    }
}
