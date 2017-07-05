package com.myapp.handbook.domain;

import android.database.sqlite.SQLiteDatabase;

import com.myapp.handbook.data.HandBookDbHelper;

import java.text.ParseException;
import java.util.List;

/**
 * Created by SAshutosh on 12/17/2016.
 */

public class TeacherTimeTable implements BaseTimeTable {

    String TeacherId;

    String SchoolId;
    List<WeeklyTimeTable> Days;

    @Override
    public List<WeeklyTimeTable> getWeeklyTimeTableList() {
        return Days;
    }

    @Override
    public void setWeeklyTimeTableList(List<WeeklyTimeTable> weeklyTimeTableList) {
        this.Days = weeklyTimeTableList;
    }

    @Override
    public String getSchoolId() {
        return SchoolId;
    }

    @Override
    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    @Override
    public String getStudentClassStandard() {
        return null;
    }

    @Override
    public void setStudentClassStandard(String studentClassStandard) {

    }

    public static boolean saveTeacherTimeTable(SQLiteDatabase db, String id, TeacherTimeTable profileTimeTable) {

        boolean success=true;
        String school_id = profileTimeTable.getSchoolId();

        for(WeeklyTimeTable day: profileTimeTable.getWeeklyTimeTableList()){
            String dayOfWeek = day.getDayOfWeek();
            for(TimeSlots timeSlot: day.getTimeSlotsList()){
                long row_id = 0;
                row_id = HandBookDbHelper.insertTimeTableEntry(db,id,dayOfWeek,school_id,timeSlot.getTeacherClassStd(),timeSlot.getTeacherId(),timeSlot.getTeacherName(),timeSlot.getStartTime(),timeSlot.getEndTime(),timeSlot.getSubject());

                if(row_id<0)
                    success =false;
            }
        }
        return success;
    }
}
