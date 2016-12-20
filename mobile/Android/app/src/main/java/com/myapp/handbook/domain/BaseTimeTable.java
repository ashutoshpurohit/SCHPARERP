package com.myapp.handbook.domain;

import java.util.List;

/**
 * Created by SAshutosh on 12/17/2016.
 */
public interface BaseTimeTable {
    List<WeeklyTimeTable> getWeeklyTimeTableList();

    void setWeeklyTimeTableList(List<WeeklyTimeTable> weeklyTimeTableList);

    String getSchoolId();

    void setSchoolId(String schoolId);

    String getStudentClassStandard();

    void setStudentClassStandard(String studentClassStandard);
}
