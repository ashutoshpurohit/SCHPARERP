package com.myapp.handbook.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.myapp.handbook.data.HandbookContract.NotificationEntry;

import java.util.Date;

/**
 * Created by SAshutosh on 3/22/2016.
 */
public class HandBookDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "handbook.db";

    public HandBookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + NotificationEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                NotificationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                NotificationEntry.COLUMN_NOTIFICATION_ID + " INTEGER NOT NULL, " +
                NotificationEntry.COLUMN_PRIORITY+ " INTEGER NOT NULL, " +
                NotificationEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                NotificationEntry.COLUMN_DETAIL + " TEXT NOT NULL, " +
                NotificationEntry.COLUMN_TITLE + " TEXT NOT NULL," +

                NotificationEntry.COLUMN_FROM + " TEXT NOT NULL" + " );";

        sqLiteDatabase.execSQL(SQL_CREATE_NOTIFICATIONS_TABLE);
        HandBookDbHelper.insertNotification(sqLiteDatabase, "Holiday tomorrow", "Holiday on 23 March 2016 on occasion of Holi", new Date().toString(), 1, "Admin", 10001);

    }

    public static void insertNotification(SQLiteDatabase sqliteDatabase, String title, String detail, String date, int priority, String from, int note_id){

        ContentValues note = new ContentValues();
        note.put(NotificationEntry.COLUMN_PRIORITY,priority);
        note.put(NotificationEntry.COLUMN_DETAIL,detail);
        note.put(NotificationEntry.COLUMN_DATE,date);
        note.put(NotificationEntry.COLUMN_TITLE,title);
        note.put(NotificationEntry.COLUMN_FROM,from);
        note.put(NotificationEntry.COLUMN_NOTIFICATION_ID,note_id);
        sqliteDatabase.insert(NotificationEntry.TABLE_NAME,null,note);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME);
        onCreate((sqLiteDatabase));

    }
}
