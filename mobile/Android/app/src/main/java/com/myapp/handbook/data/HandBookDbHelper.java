package com.myapp.handbook.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.myapp.handbook.Profile;
import com.myapp.handbook.data.HandbookContract.NotificationEntry;
import com.myapp.handbook.data.HandbookContract.ProfileEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        final String SQL_CREATE_PROFILE_TABLE = "CREATE TABLE " + ProfileEntry.TABLE_NAME + " (" +
                ProfileEntry.COLUMN_ID +" TEXT NOT NULL,"+
                ProfileEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_MIDDLE_NAME + " TEXT, " +
                ProfileEntry.COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                HandbookContract.ProfileEntry.COLUMN_ROLE + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_GENDER + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_STD + " TEXT , " +
                HandbookContract.ProfileEntry.COLUMN_DOB + " INTEGER NOT NULL, " +
                ProfileEntry.COLUMN_ADDRESS + " TEXT" + " );";


        sqLiteDatabase.execSQL(SQL_CREATE_NOTIFICATIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PROFILE_TABLE);
        HandBookDbHelper.insertNotification(sqLiteDatabase, "Holiday tomorrow", "Holiday on 23 March 2016 on occasion of Holi", new Date().toString(), 1, "Admin", 10001);

    }

    public static void insertNotification(SQLiteDatabase sqliteDatabase, String title, String detail, String date, int priority, String from, int note_id){

        ContentValues note = new ContentValues();
        note.put(NotificationEntry.COLUMN_PRIORITY,priority);
        note.put(NotificationEntry.COLUMN_DETAIL,detail);
        note.put(NotificationEntry.COLUMN_DATE,date);
        note.put(NotificationEntry.COLUMN_TITLE,title);
        note.put(NotificationEntry.COLUMN_FROM, from);
        note.put(NotificationEntry.COLUMN_NOTIFICATION_ID, note_id);
        sqliteDatabase.insert(NotificationEntry.TABLE_NAME, null, note);
    }

    public static void insertProfile(SQLiteDatabase sqliteDatabase, String id,String firstname, String lastname, String middlename, String role, String gender,
                                     String std, String address, String dob) {

        ContentValues note = new ContentValues();
        note.put(ProfileEntry.COLUMN_ID,id);
        note.put(ProfileEntry.COLUMN_FIRST_NAME,firstname);
        note.put(HandbookContract.ProfileEntry.COLUMN_MIDDLE_NAME,middlename);
        note.put(HandbookContract.ProfileEntry.COLUMN_LAST_NAME,lastname);
        note.put(ProfileEntry.COLUMN_ADDRESS,address);
        note.put(ProfileEntry.COLUMN_STD,std);
        note.put(ProfileEntry.COLUMN_GENDER,gender);
        note.put(HandbookContract.ProfileEntry.COLUMN_ROLE,role);
        note.put(ProfileEntry.COLUMN_MIDDLE_NAME,middlename);
        note.put(ProfileEntry.COLUMN_DOB, dob);

        long retVal= sqliteDatabase.insert(ProfileEntry.TABLE_NAME, null, note);
    }

    public static List<Profile> LoadProfilefromDb(SQLiteDatabase sqliteDatabase) {
        ArrayList<Profile> profiles = new ArrayList<>();

        Cursor cursor= sqliteDatabase.query(HandbookContract.ProfileEntry.TABLE_NAME,
                null,
                null, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(HandbookContract.ProfileEntry.COLUMN_ID));
                String firstName = cursor.getString(cursor.getColumnIndex(HandbookContract.ProfileEntry.COLUMN_FIRST_NAME));
                String middleName = cursor.getString(cursor.getColumnIndex(HandbookContract.ProfileEntry.COLUMN_MIDDLE_NAME));
                String lastName = cursor.getString(cursor.getColumnIndex(HandbookContract.ProfileEntry.COLUMN_LAST_NAME));
                String address = cursor.getString(cursor.getColumnIndex(HandbookContract.ProfileEntry.COLUMN_ADDRESS));
                String dob = cursor.getString(cursor.getColumnIndex(HandbookContract.ProfileEntry.COLUMN_DOB));
                String gender = cursor.getString(cursor.getColumnIndex(HandbookContract.ProfileEntry.COLUMN_GENDER));
                String role = cursor.getString(cursor.getColumnIndex(HandbookContract.ProfileEntry.COLUMN_ROLE));
                String std = cursor.getString(cursor.getColumnIndex(HandbookContract.ProfileEntry.COLUMN_STD));
                Profile profile= new Profile(id,firstName,middleName,lastName,role,gender,dob,std,address);
                profiles.add(profile);

            }
        } finally {
            cursor.close();
        }

        return profiles;
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME);
        onCreate((sqLiteDatabase));

    }
}
