package com.myapp.handbook.domain;

import java.util.List;

/**
 * Created by SAshutosh on 10/15/2016.
 */

public class TimeTable {

    String ClassStandard;
    String SchoolId;
    List<WeeklyTimeTable> Days;

    public List<WeeklyTimeTable> getWeeklyTimeTableList() {
        return Days;
    }

    public void setWeeklyTimeTableList(List<WeeklyTimeTable> weeklyTimeTableList) {
        this.Days = weeklyTimeTableList;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        this.SchoolId = schoolId;
    }

    public String getClassStandard() {
        return ClassStandard;
    }

    public void setClassStandard(String classStandard) {
        this.ClassStandard = classStandard;
    }
}


