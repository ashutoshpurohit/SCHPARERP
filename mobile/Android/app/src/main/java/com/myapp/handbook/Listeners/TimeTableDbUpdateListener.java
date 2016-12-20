package com.myapp.handbook.Listeners;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.myapp.handbook.QuickstartPreferences;
import com.myapp.handbook.Tasks.FetchTimeTableAsyncTask;
import com.myapp.handbook.domain.BaseTimeTable;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.TeacherTimeTable;
import com.myapp.handbook.domain.TimeTable;

import static com.myapp.handbook.domain.TeacherTimeTable.saveTeacherTimeTable;
import static com.myapp.handbook.domain.TimeTable.saveStudentTimeTable;


/**
 * Created by SAshutosh on 12/17/2016.
 */

public class TimeTableDbUpdateListener implements FetchTimeTableAsyncTask.TaskListener {
    RoleProfile profile;
    SharedPreferences sharedPreferences;
    SQLiteDatabase db;
    public TimeTableDbUpdateListener(SQLiteDatabase db, RoleProfile profile, SharedPreferences sharedPreferences){
     this.profile =profile;
     this.sharedPreferences=sharedPreferences;
     this.db=db;
    }

    @Override
    public void onFinished(BaseTimeTable table) {
        if(table!=null) {
            boolean result =false;
            if(profile.getRole().equals(RoleProfile.ProfileRole.STUDENT.toString())) {
                result =saveStudentTimeTable(db,profile.getId(), (TimeTable) table);
            }
            else if(profile.getRole().equals(RoleProfile.ProfileRole.TEACHER.toString())){
                result = saveTeacherTimeTable(db,profile.getId(),(TeacherTimeTable)table);
            }
            if(result)
            {
                sharedPreferences.edit().putBoolean(QuickstartPreferences.TIMETABLE_DOWNLOADED+"_" +profile.getId() , true).commit();
            }
        }

    }
}
