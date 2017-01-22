package com.myapp.handbook.domain;

import java.util.List;

/**
 * Created by SAshutosh on 1/14/2017.
 */

public class Event {
    String EventName;
    String EventDate;
    String EventPlace;
    String EventStartTime;
    String EventEndTime;
    String SchoolId;
    List<String> StudentIDS;
    List<String> TeacherIdS;




    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public String getEventDate() {
        return EventDate;
    }

    public void setEventDate(String eventDate) {
        EventDate = eventDate;
    }

    public String getEventPlace() {
        return EventPlace;
    }

    public void setEventPlace(String eventPlace) {
        EventPlace = eventPlace;
    }

    public String getEventStartTime() {
        return EventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        EventStartTime = eventStartTime;
    }

    public String getEventEndTime() {
        return EventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        EventEndTime = eventEndTime;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    public List<String> getStudentIds() {
        return StudentIDS;
    }

    public void setStudentIds(List<String> studentIds) {
        StudentIDS = studentIds;
    }

    public List<String> getTeacherIds() {
        return TeacherIdS;
    }

    public void setTeacherIds(List<String> teacherIds) {
        TeacherIdS = teacherIds;
    }

}
