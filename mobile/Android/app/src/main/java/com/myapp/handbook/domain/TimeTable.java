package com.myapp.handbook.domain;

import android.database.sqlite.SQLiteDatabase;

import com.myapp.handbook.data.HandBookDbHelper;

import java.text.ParseException;
import java.util.List;

/**
 * Created by SAshutosh on 10/15/2016.
 */

public class TimeTable implements BaseTimeTable {

    String ClassStandard;
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
        this.SchoolId = schoolId;
    }

    @Override
    public String getStudentClassStandard() {
        return ClassStandard;
    }

    @Override
    public void setStudentClassStandard(String studentClassStandard) {
        this.ClassStandard = studentClassStandard;
    }

    public static boolean saveStudentTimeTable(SQLiteDatabase db,String id, TimeTable profileTimeTable) {

        boolean success=true;
        String school_id = profileTimeTable.getSchoolId();
        String std = profileTimeTable.getStudentClassStandard();
        for(WeeklyTimeTable day: profileTimeTable.getWeeklyTimeTableList()){
            String dayOfWeek = day.getDayOfWeek();
            for(TimeSlots timeSlot: day.getTimeSlotsList()){
                long row_id = 0;
                row_id = HandBookDbHelper.insertTimeTableEntry(db,id,dayOfWeek,school_id,std,
                timeSlot.getTeacherId(),timeSlot.getTeacherName(),
                timeSlot.getStartTime(),timeSlot.getEndTime(),timeSlot.getSubject());

                if(row_id<0)
                    success =false;
            }
        }
        return success;
    }

    public static List<TimeSlots> getTimeSlot(BaseTimeTable profileTimeTable, String dayOfWeek) {
        List<TimeSlots> slots=null;
        for (WeeklyTimeTable table: profileTimeTable.getWeeklyTimeTableList()
                ) {
            if(table.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                slots = table.getTimeSlotsList();
                break;
            }

        }
        return slots;
    }


}


