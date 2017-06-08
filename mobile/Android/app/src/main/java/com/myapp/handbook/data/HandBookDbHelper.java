package com.myapp.handbook.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.myapp.handbook.data.HandbookContract.NotificationEntry;
import com.myapp.handbook.data.HandbookContract.ProfileEntry;
import com.myapp.handbook.domain.BaseTimeTable;
import com.myapp.handbook.domain.DiaryNote;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.SchoolProfile;
import com.myapp.handbook.domain.TeacherTimeTable;
import com.myapp.handbook.domain.TimeSlots;
import com.myapp.handbook.domain.TimeTable;
import com.myapp.handbook.domain.WeeklyTimeTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

                NotificationEntry.COLUMN_FROM + " TEXT NOT NULL," +
                NotificationEntry.COLUMN_IMAGE+ " TEXT," +
                NotificationEntry.COLUMN_MSG_TYPE+ " INTEGER, "+
                NotificationEntry.COLUMN_TO_IDS+ " TEXT, "+
                NotificationEntry.COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +" );";

        final String SQL_CREATE_PROFILE_TABLE = "CREATE TABLE " + ProfileEntry.TABLE_NAME + " (" +
                ProfileEntry.COLUMN_ID +" TEXT NOT NULL,"+
                ProfileEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_MIDDLE_NAME + " TEXT, " +
                ProfileEntry.COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                HandbookContract.ProfileEntry.COLUMN_ROLE + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_GENDER + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_STD + " TEXT , " +
                HandbookContract.ProfileEntry.COLUMN_DOB + " INTEGER NOT NULL, " +
                NotificationEntry.COLUMN_IMAGE+ " TEXT," +
                ProfileEntry.COLUMN_ADDRESS + " TEXT" + " );";


        final String SQL_CREATE_TIMETABLE_TABLE = "CREATE TABLE " + HandbookContract.TimetableEntry.TABLE_NAME + " (" +
                HandbookContract.TimetableEntry.COLUMN_ID +" TEXT NOT NULL,"+
                HandbookContract.TimetableEntry.COLUMN_STD + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_SCHOOL_ID + " TEXT, " +
                HandbookContract.TimetableEntry.COLUMN_DAY + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_START_TIME + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_END_TIME + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_SUBJECT + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_TEACHER_NAME + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_TEACHER_ID + " TEXT" + " );";

        final String SQL_CREATE_CONTACT_SCHOOL_TABLE = "CREATE TABLE " + HandbookContract.ContactSchoolEntry.TABLE_NAME + " (" +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ID + " TEXT NOT NULL, " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_NAME + " TEXT NOT NULL, " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_1 + " TEXT , " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_2 + " TEXT , " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_3 + " TEXT , " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_CONTACT_NUMBER_1 + " TEXT , " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_CONTACT_NUMBER_2 + " TEXT , " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_EMAIL_ID + " TEXT,"+
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_WEBSITE +" TEXT,"+
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_LOGO + " BLOB);";



        sqLiteDatabase.execSQL(SQL_CREATE_NOTIFICATIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PROFILE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TIMETABLE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CONTACT_SCHOOL_TABLE);
        HandBookDbHelper.insertNotification(sqLiteDatabase, "Holiday tomorrow", "Holiday on 23 March 2016 on occasion of Holi", new Date().toString(), 1, "Admin", 10001,"",101,"110,105");

    }

    public static void insertNotification(SQLiteDatabase sqliteDatabase, String title, String detail, String date, int priority, String from, int note_id, String image, int msg_type, String toIds ){

        ContentValues note = new ContentValues();
        note.put(NotificationEntry.COLUMN_PRIORITY,priority);
        note.put(NotificationEntry.COLUMN_DETAIL,detail);
        note.put(NotificationEntry.COLUMN_DATE,date);
        note.put(NotificationEntry.COLUMN_TITLE,title);
        note.put(NotificationEntry.COLUMN_FROM, from);
        note.put(NotificationEntry.COLUMN_NOTIFICATION_ID, note_id);
        note.put(NotificationEntry.COLUMN_IMAGE,image);
        note.put(NotificationEntry.COLUMN_MSG_TYPE,msg_type);
        note.put(NotificationEntry.COLUMN_TO_IDS,toIds);

        sqliteDatabase.insert(NotificationEntry.TABLE_NAME, null, note);
    }

    public static void insertProfile(SQLiteDatabase sqliteDatabase, String id,String firstname, String lastname, String middlename, String role, String gender,
                                     String std, String address, String dob, String image) {

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
        note.put(ProfileEntry.COLUMN_IMAGE,image);

        long retVal= sqliteDatabase.insert(ProfileEntry.TABLE_NAME, null, note);
    }

    public static long insertTimeTableEntry(SQLiteDatabase sqliteDatabase, String id,String dayOfWeek, String school_id, String std, String teacher_id, String teacher_name, String start_time,
                                     String end_time, String subject) {

        ContentValues note = new ContentValues();
        note.put(HandbookContract.TimetableEntry.COLUMN_ID,id);
        note.put(HandbookContract.TimetableEntry.COLUMN_DAY,dayOfWeek);
        note.put(HandbookContract.TimetableEntry.COLUMN_SCHOOL_ID,school_id);
        note.put(HandbookContract.TimetableEntry.COLUMN_TEACHER_ID,teacher_id);
        note.put(HandbookContract.TimetableEntry.COLUMN_TEACHER_NAME,teacher_name);
        note.put(HandbookContract.TimetableEntry.COLUMN_START_TIME,start_time);
        note.put(HandbookContract.TimetableEntry.COLUMN_END_TIME,end_time);
        note.put(HandbookContract.TimetableEntry.COLUMN_SUBJECT,subject);
        note.put(HandbookContract.TimetableEntry.COLUMN_STD,std);

        long retVal= sqliteDatabase.insert(HandbookContract.TimetableEntry.TABLE_NAME, null, note);
        return retVal;
    }
    public static long insertSchoolContactEntry(SQLiteDatabase sqliteDatabase,String school_id, String schoolName, String address_1,
                                                String address_2, String address_3, String contact_number_1, String contact_number_2,
                                                String school_email_id, String school_website, String school_logo) {

        ContentValues contacts = new ContentValues();
        contacts.put(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ID,school_id);
        contacts.put(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_NAME,schoolName);
        contacts.put(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_1,address_1);
        contacts.put(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_2,address_2);
        contacts.put(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_3,address_3);
        contacts.put(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_CONTACT_NUMBER_1,contact_number_1);
        contacts.put(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_CONTACT_NUMBER_2,contact_number_2);
        contacts.put(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_EMAIL_ID,school_email_id);
        contacts.put(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_WEBSITE,school_website);
        contacts.put(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_LOGO,school_logo);

        long retVal= sqliteDatabase.insert(HandbookContract.ContactSchoolEntry.TABLE_NAME, null, contacts);
        return retVal;
    }

    public static SchoolProfile loadSchoolContactsFromDb(SQLiteDatabase sqliteDatabase){

        return null;
    }

    public static List<RoleProfile> LoadProfilefromDb(SQLiteDatabase sqliteDatabase) {
        ArrayList<RoleProfile> profiles = new ArrayList<>();

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
                String imageUrl = cursor.getString(cursor.getColumnIndex(ProfileEntry.COLUMN_IMAGE));
                RoleProfile profile= new RoleProfile(id,firstName,middleName,lastName,role,gender,dob,std,address,imageUrl);
                profiles.add(profile);

            }
        } finally {
            cursor.close();
        }

        return profiles;
    }

    //Load School profile from DB if already exists..
    public static SchoolProfile LoadSchoolProfilefromDb(SQLiteDatabase sqliteDatabase) {
        SchoolProfile profile = new SchoolProfile();

        Cursor cursor= sqliteDatabase.query(HandbookContract.ContactSchoolEntry.TABLE_NAME,
                null,
                null, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ID));
                String schoolName = cursor.getString(cursor.getColumnIndex(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_NAME));
                String schoolAddress1 = cursor.getString(cursor.getColumnIndex(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_1));
                String schoolAddress2 = cursor.getString(cursor.getColumnIndex(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_2));
                String schoolAddress3 = cursor.getString(cursor.getColumnIndex(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_3));
                String schoolPrimaryContact = cursor.getString(cursor.getColumnIndex(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_CONTACT_NUMBER_1));
                String schoolSecondaryContact = cursor.getString(cursor.getColumnIndex(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_CONTACT_NUMBER_2));
                String schoolEmailId = cursor.getString(cursor.getColumnIndex(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_EMAIL_ID));
                String schoolWebsite = cursor.getString(cursor.getColumnIndex(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_WEBSITE));
                String schoolLogo = cursor.getString(cursor.getColumnIndex(HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_LOGO));
                profile.setSchoolId(id);
                profile.setSchoolName(schoolName);
                profile.setSchoolFullAddress(schoolAddress1);
                profile.setSchoolAddress_2(schoolAddress2);
                profile.setSchoolAddress_3(schoolAddress3);
                profile.setSchoolMainTelephoneNumber(schoolPrimaryContact);
                profile.setSchoolSecondaryTelephoneNumber(schoolSecondaryContact);
                profile.setSchoolEmailId(schoolEmailId);
                profile.setSchoolWebSite(schoolWebsite);
                profile.setSchoolLogoImageURL(schoolLogo);
            }
        } finally {
            cursor.close();
        }

        return profile;
    }


    public static List<DiaryNote> loadLatestDiaryNote(SQLiteDatabase sqliteDatabase, int type, String profileId, int count){
        List<DiaryNote> diaryNotes = new ArrayList<>();
        String query_to_fetch_earliest="select *  from "+HandbookContract.NotificationEntry.TABLE_NAME+" where "+
                NotificationEntry.COLUMN_MSG_TYPE+ " = '"+ type+ "' and "+ NotificationEntry.COLUMN_TO_IDS+" LIKE "+"'%"+profileId+"%'"  +" order  by datetime("+HandbookContract.NotificationEntry.COLUMN_TIMESTAMP+") DESC ";
        int fetchedCount=0;
        Cursor cursor = sqliteDatabase.rawQuery(query_to_fetch_earliest, null);
        try {
            while (cursor.moveToNext() && fetchedCount < count){
                DiaryNote currentNote = new DiaryNote();
                currentNote.setDate(cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_DATE)));
                currentNote.setTitle(cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_TITLE)));
                currentNote.setDetail(cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_DETAIL)));
                diaryNotes.add(currentNote);
                fetchedCount++;

            }
        }
        finally {
            cursor.close();
        }
        return diaryNotes;
    }

    public static BaseTimeTable loadTimeTable(SQLiteDatabase sqliteDatabase, String id, RoleProfile.ProfileRole role)
    {
        BaseTimeTable table=null;
        if(role== RoleProfile.ProfileRole.STUDENT){
            table = loadStudentTimeTable(sqliteDatabase,id);
        }
        else if(role == RoleProfile.ProfileRole.TEACHER){
            table = loadTeacherTimeTable(sqliteDatabase,id);
        }
        return table;
    }

    private static TimeTable loadStudentTimeTable(SQLiteDatabase sqliteDatabase, String id) {

        TimeTable table = new TimeTable();
        HashMap<String , ArrayList<TimeSlots>> dayTimeSlotMap = new HashMap<>();
        Cursor cursor= sqliteDatabase.query(HandbookContract.TimetableEntry.TABLE_NAME,null,"id=?", new String[] {id},null,null,null);

        try {

            while(cursor.moveToNext()){

                String std= cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_STD));
                String school_id = cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_SCHOOL_ID));
                String dayOfWeek = cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_DAY));
                String start_time =cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_START_TIME));
                String end_time =cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_END_TIME));
                String subject =cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_SUBJECT));
                String teacherName =cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_TEACHER_NAME));
                String teacher_id =cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_TEACHER_ID));
                TimeSlots t = new TimeSlots(start_time,end_time,subject,teacher_id,teacherName);
                ArrayList<TimeSlots> timeSlotsForTheDay = dayTimeSlotMap.get(dayOfWeek);
                if(timeSlotsForTheDay==null) {
                    timeSlotsForTheDay = new ArrayList<>();
                    dayTimeSlotMap.put(dayOfWeek,timeSlotsForTheDay);
                }

                timeSlotsForTheDay.add(t);
            }
            List<WeeklyTimeTable> weeklyTimeTable = new ArrayList<>();
            for(String dayOfWeek:dayTimeSlotMap.keySet()){
                WeeklyTimeTable w = new WeeklyTimeTable(dayOfWeek,dayTimeSlotMap.get(dayOfWeek));
                weeklyTimeTable.add(w);
            }
            table.setWeeklyTimeTableList(weeklyTimeTable);
        }
        finally {
            cursor.close();
        }
        return  table;
    }

    private static TeacherTimeTable loadTeacherTimeTable(SQLiteDatabase sqliteDatabase, String id) {

        TeacherTimeTable table = new TeacherTimeTable();
        HashMap<String , ArrayList<TimeSlots>> dayTimeSlotMap = new HashMap<>();
        Cursor cursor= sqliteDatabase.query(HandbookContract.TimetableEntry.TABLE_NAME,null,"id=?", new String[] {id},null,null,null);

        try {

            while(cursor.moveToNext()){

                String std= cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_STD));
                String school_id = cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_SCHOOL_ID));
                String dayOfWeek = cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_DAY));
                String start_time =cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_START_TIME));
                String end_time =cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_END_TIME));
                String subject =cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_SUBJECT));
                String teacherName =cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_TEACHER_NAME));
                String teacher_id =cursor.getString(cursor.getColumnIndex(HandbookContract.TimetableEntry.COLUMN_TEACHER_ID));
                TimeSlots t = new TimeSlots(start_time,end_time,subject,std);
                ArrayList<TimeSlots> timeSlotsForTheDay = dayTimeSlotMap.get(dayOfWeek);
                if(timeSlotsForTheDay==null) {
                    timeSlotsForTheDay = new ArrayList<>();
                    dayTimeSlotMap.put(dayOfWeek,timeSlotsForTheDay);
                }

                timeSlotsForTheDay.add(t);
            }
            List<WeeklyTimeTable> weeklyTimeTable = new ArrayList<>();
            for(String dayOfWeek:dayTimeSlotMap.keySet()){
                WeeklyTimeTable w = new WeeklyTimeTable(dayOfWeek,dayTimeSlotMap.get(dayOfWeek));
                weeklyTimeTable.add(w);
            }
            table.setWeeklyTimeTableList(weeklyTimeTable);
        }
        finally {
            cursor.close();
        }
        return  table;
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME);
        onCreate((sqLiteDatabase));

    }


}
