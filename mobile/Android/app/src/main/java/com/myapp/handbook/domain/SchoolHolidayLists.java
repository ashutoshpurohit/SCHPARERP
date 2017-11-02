package com.myapp.handbook.domain;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.myapp.handbook.QuickstartPreferences;
import com.myapp.handbook.data.HandBookDbHelper;

import java.util.List;

/**
 * Created by SAshutosh on 1/14/2017.
 */

public class SchoolHolidayLists {


    List<HolidayLists> holidayLists;

    public static void saveHolidayListsToDB(SQLiteDatabase sqliteDatabase, List<HolidayLists> listHoliday,
                                            SharedPreferences sharedPreferences) {
        try {
            HandBookDbHelper.insertHolidayListsToDB(sqliteDatabase, listHoliday);
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SCHOOL_HOLIDAY_LISTS_DOWNLOADED, true).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public List<HolidayLists> getHolidayLists() {
        return holidayLists;
    }

    public void setEvents(List<HolidayLists> holidayLists) {
        this.holidayLists = holidayLists;
    }

}
