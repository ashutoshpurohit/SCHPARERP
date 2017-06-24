package com.myapp.handbook.domain;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.myapp.handbook.QuickstartPreferences;
import com.myapp.handbook.data.HandBookDbHelper;

import java.util.List;

/**
 * Created by SAshutosh on 1/14/2017.
 */

public class CalendarEvents {


    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    List<Event> events;

    public static void saveSchoolCalendarEventsToDB(SQLiteDatabase sqliteDatabase, List<Event> schoolCalendar,
                                                    SharedPreferences sharedPreferences) {
        HandBookDbHelper.insertSchoolCalendarEventsToDB(sqliteDatabase, schoolCalendar);

        sharedPreferences.edit().putBoolean(QuickstartPreferences.SCHOOL_CALENDER_EVENTS_DOWNLOADED, true).commit();


    }

}
