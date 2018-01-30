package com.myapp.handbook.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.myapp.handbook.TeacherProfile;
import com.myapp.handbook.data.HandbookContract.NotificationEntry;
import com.myapp.handbook.data.HandbookContract.ProfileEntry;
import com.myapp.handbook.domain.BaseTimeTable;
import com.myapp.handbook.domain.DiaryNote;
import com.myapp.handbook.domain.Event;
import com.myapp.handbook.domain.HolidayLists;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.SchoolProfile;
import com.myapp.handbook.domain.TeacherTimeTable;
import com.myapp.handbook.domain.TimeSlots;
import com.myapp.handbook.domain.TimeTable;
import com.myapp.handbook.domain.WeeklyTimeTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SAshutosh on 3/22/2016.
 */
public class HandBookDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "handbook.db";
    private static final int DATABASE_VERSION = 2;

    public HandBookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    public static void insertProfile(SQLiteDatabase sqliteDatabase, String id,String firstname, String lastname,
                                     String middlename, String role, String gender,
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
        try {
            long retVal = sqliteDatabase.insert(ProfileEntry.TABLE_NAME, null, note);
        }
        catch (Exception e){
            Log.d("DB_INSERT",e.getMessage());
        }
    }

    public static void insertTeacherForStudent(SQLiteDatabase sqliteDatabase, String studentId,String teacherId,String teacherFirstname, String teacchetLastname,
                                     String mobileNumber, String email, String subject,
                                     String std) {

        ContentValues note = new ContentValues();
        note.put(HandbookContract.TeacherForStudentEntry.COLUMN_STUDENTID,studentId);
        note.put(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_ID,teacherId);
        note.put(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_LAST_NAME,teacchetLastname);
        note.put(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_FIRST_NAME,teacherFirstname);
        note.put(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_MOBILE,mobileNumber);
        note.put(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_EMAIL,email);
        note.put(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_SUBJECT,subject);
        note.put(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_ROLE_STD,std);

        try {
            long retVal = sqliteDatabase.insert(HandbookContract.TeacherForStudentEntry.TABLE_NAME, null, note);
            Log.d("DB_INSERT value",Long.toString(retVal));
        }
        catch (Exception e){
            Log.d("DB_INSERT",e.getMessage());
        }
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

        return sqliteDatabase.insert(HandbookContract.TimetableEntry.TABLE_NAME, null, note);
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

        return sqliteDatabase.insert(HandbookContract.ContactSchoolEntry.TABLE_NAME, null, contacts);
    }


    public static long insertHolidayListsToDB(SQLiteDatabase sqliteDatabase, List<HolidayLists> schoolHolidayLists) {
        long retVal = 0;
        ContentValues holiday = new ContentValues();
        for (int i = 0; i < schoolHolidayLists.size(); i++) {
            holiday.put(HandbookContract.HolidayListsEntry.COLUMN_SCHOOL_ID, schoolHolidayLists.get(i).getSchoolId());
            holiday.put(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_ID, schoolHolidayLists.get(i).getHolidayId());
            holiday.put(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_NAME, schoolHolidayLists.get(i).getHoliday());
            holiday.put(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_DESCRIPTION, schoolHolidayLists.get(i).getHolidayDescription());
            holiday.put(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_DATE, schoolHolidayLists.get(i).getHolidayDate());
            holiday.put(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_MONTH, schoolHolidayLists.get(i).getHolidayMonth());
            holiday.put(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_YEAR, schoolHolidayLists.get(i).getHolidayYear());
            holiday.put(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_TYPE, schoolHolidayLists.get(i).getHolidayType());

            try {
                retVal = sqliteDatabase.insert(HandbookContract.HolidayListsEntry.TABLE_NAME, null, holiday);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return retVal;
    }




    public static long insertSchoolCalendarEventsToDB(SQLiteDatabase sqliteDatabase,List<Event> schoolCalendar){
        long retVal = 0;
        ContentValues events = new ContentValues();
        for (int i=0;i<schoolCalendar.size();i++) {
            events.put(HandbookContract.CalenderEventsEntry.COLUMN_SCHOOL_ID ,schoolCalendar.get(i).getSchoolId());
            events.put(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_ID ,schoolCalendar.get(i).getEventId());
            events.put(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_NAME, schoolCalendar.get(i).getEventName());
            events.put(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_LOCATION, schoolCalendar.get(i).getEventPlace());
            events.put(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE, schoolCalendar.get(i).getEventDate());
            events.put(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_START_TIME, schoolCalendar.get(i).getEventStartTime());
            events.put(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_END_TIME, schoolCalendar.get(i).getEventEndTime());
            events.put(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_LIKE_BUTTON_CLICKED,
                    schoolCalendar.get(i).getEventLikeButtonClicked());
            events.put(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_ADD_TO_CALENDER,
                    schoolCalendar.get(i).getAddToCalender());

            //Hardcoding student id and teacher id,this needs to be list of student and teacher id
            events.put(HandbookContract.CalenderEventsEntry.COLUMN_STUDENT_ID,
                    "1");
            events.put(HandbookContract.CalenderEventsEntry.COLUMN_TEACHER_ID,
                    "2");

            try {
                retVal = sqliteDatabase.insert(HandbookContract.CalenderEventsEntry.TABLE_NAME, null, events);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return retVal;
    }

    public static void clearAllSchoolEvents(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.delete(HandbookContract.CalenderEventsEntry.TABLE_NAME,null,null);
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
    public static SchoolProfile loadSchoolProfileFromDB(SQLiteDatabase sqliteDatabase) {
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

    public static List<TeacherProfile> loadTeachersForStudent(SQLiteDatabase sqliteDatabase, String selectedStudentId) {

        List<TeacherProfile> teacherProfiles = new ArrayList<>();
        Cursor cursor=null;
        String query_to_fetch_teachers = "select *  from " + HandbookContract.TeacherForStudentEntry.TABLE_NAME + " where "+
        HandbookContract.TeacherForStudentEntry.COLUMN_STUDENTID + " = '"+ selectedStudentId + "'";
        /*Cursor cursor= sqliteDatabase.query(HandbookContract.TeacherForStudentEntry.TABLE_NAME,
                null,
                null, null, null, null, null, null);*/


        try {
            cursor = sqliteDatabase.rawQuery(query_to_fetch_teachers, null);


            while (cursor.moveToNext()) {
                TeacherProfile profile = new TeacherProfile();
                profile.setId(cursor.getString(cursor.getColumnIndex(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_ID)));
                profile.setEmail(cursor.getString(cursor.getColumnIndex(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_EMAIL)));
                profile.setFirstName(cursor.getString(cursor.getColumnIndex(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_FIRST_NAME)));
                profile.setLastName(cursor.getString(cursor.getColumnIndex(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_LAST_NAME)));
                profile.setMobileNumber(cursor.getString(cursor.getColumnIndex(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_MOBILE)));
                profile.setStd(cursor.getString(cursor.getColumnIndex(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_ROLE_STD)));
                profile.setSubject(cursor.getString(cursor.getColumnIndex(HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_SUBJECT)));
                teacherProfiles.add(profile);

            }
        } catch (Exception ex) {
            System.out.print("Error in fetching recorde" +ex.getMessage());
            ex.printStackTrace();
            Log.d("Error fetching teachers",ex.getMessage());
        }
        finally {
            if(cursor!=null)
                cursor.close();
        }
        return teacherProfiles;
    }

    //Load Holiday List from DB if already exists..
    public static List<HolidayLists> loadHolidayListsfromDb(SQLiteDatabase sqliteDatabase) {
        List<HolidayLists> schooolHolidayLists = new ArrayList<>();
        String query_to_fetch_earliest = "select *  from " + HandbookContract.HolidayListsEntry.TABLE_NAME + "" +
                " order  by datetime(" + HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_DATE + ") ASC ";
        Cursor cursor = sqliteDatabase.rawQuery(query_to_fetch_earliest, null);

        try {
            while (cursor.moveToNext()) {
                HolidayLists newHoliday = new HolidayLists();

                newHoliday.setSchoolId(cursor.getString(cursor.getColumnIndex(HandbookContract.HolidayListsEntry.COLUMN_SCHOOL_ID)));
                newHoliday.setHolidayId(cursor.getString(cursor.getColumnIndex(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_ID)));
                newHoliday.setHoliday(cursor.getString(cursor.getColumnIndex(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_NAME)));
                newHoliday.setHolidayDescription(cursor.getString(cursor.getColumnIndex(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_DESCRIPTION
                )));
                newHoliday.setHolidayDate(cursor.getString(cursor.getColumnIndex(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_DATE)));

                newHoliday.setHolidayMonth(cursor.getString(cursor.getColumnIndex(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_MONTH)));
                newHoliday.setHolidayYear(cursor.getString(cursor.getColumnIndex(HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_YEAR)));
                newHoliday.setHolidayType(cursor.getString(cursor.getColumnIndex
                        (HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_TYPE)));

                schooolHolidayLists.add(newHoliday);
            }
        } finally {
            cursor.close();
        }

        return schooolHolidayLists;
    }





    //Load School calendar from DB if already exists..
    public static List<Event> loadSchoolCalendarfromDb(SQLiteDatabase sqliteDatabase, int selectedMonth) {
        List<Event> schooolEvents = new ArrayList<>();

        String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //Log.v("CalenderAct",currDate);

       /* String query_to_fetch_earliest = "select *  from " + HandbookContract.CalenderEventsEntry.TABLE_NAME + "" +
                " order  by datetime(" + HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE + ") DESC ";*/

        /*String query_to_fetch_earliest="SELECT * FROM calenderevents where  strftime('%Y',event_date) == strftime('%Y',date('now')) " +
                "AND  strftime('%m',event_date) = strftime('%m',date(1))";
        */
        String query_to_fetch_earliest;
        if (selectedMonth < 10) {
            /*  To format month recieved as single digit to double digit for sqlite db to identify month in double digit
        * */
            query_to_fetch_earliest = "SELECT * FROM " + HandbookContract.CalenderEventsEntry.TABLE_NAME +
                    " where  strftime('%Y'," + HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE + ") == strftime('%Y',date('now'))" +
                    " AND  strftime('%m'," + HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE + ") = '0" + selectedMonth + "' " +
                    "order by datetime(" + HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE + ") DESC";
        } else {
            query_to_fetch_earliest = "SELECT * FROM " + HandbookContract.CalenderEventsEntry.TABLE_NAME +
                    " where  strftime('%Y'," + HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE + ") == strftime('%Y',date('now'))" +
                    " AND  strftime('%m'," + HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE + ") = '" + selectedMonth + "' " +
                    "order by datetime(" + HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE + ") DESC";
        }

        /*String query_to_fetch_earliest = "select *  from " + HandbookContract.CalenderEventsEntry.TABLE_NAME + "  WHERE " +
                HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE + " >= '" + currDate +
                "' order  by datetime(" + HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE + ") DESC ";*/
        // Log.v("CalenderAct",query_to_fetch_earliest);
        //L
        Cursor cursor = sqliteDatabase.rawQuery(query_to_fetch_earliest, null);

        try {
            while (cursor.moveToNext()) {
                Event newEvent = new Event();

                newEvent.setSchoolId(cursor.getString(cursor.getColumnIndex(HandbookContract.CalenderEventsEntry.COLUMN_SCHOOL_ID)));
                newEvent.setEventId(cursor.getString(cursor.getColumnIndex(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_ID)));
                newEvent.setEventName(cursor.getString(cursor.getColumnIndex(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_NAME)));
                newEvent.setEventPlace(cursor.getString(cursor.getColumnIndex(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_LOCATION)));
                newEvent.setEventDate(cursor.getString(cursor.getColumnIndex(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE)));
                Log.v("check event date", String.valueOf(newEvent.getEventDate()));
                newEvent.setEventStartTime(cursor.getString(cursor.getColumnIndex(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_START_TIME)));
                newEvent.setEventEndTime(cursor.getString(cursor.getColumnIndex(HandbookContract.CalenderEventsEntry.COLUMN_EVENT_END_TIME)));
                newEvent.setEventLikeButtonClicked(cursor.getString(cursor.getColumnIndex
                        (HandbookContract.CalenderEventsEntry.COLUMN_EVENT_LIKE_BUTTON_CLICKED)));
                newEvent.setAddToCalender(cursor.getString(cursor.getColumnIndex
                        (HandbookContract.CalenderEventsEntry.COLUMN_EVENT_ADD_TO_CALENDER)));
                schooolEvents.add(newEvent);
               }
        } finally {
            cursor.close();
        }

        return schooolEvents;
    }

    public static List<DiaryNote> loadLatestDiaryNote(SQLiteDatabase sqliteDatabase, int type, String profileId, int count){
        List<DiaryNote> diaryNotes = new ArrayList<>();
        String query_to_fetch_earliest="select *  from "+HandbookContract.NotificationEntry.TABLE_NAME+" where "+
                NotificationEntry.COLUMN_MSG_TYPE + " = '" + type + "' and " + NotificationEntry.COLUMN_TO_IDS + " LIKE " +
                "'%" + profileId + "%'" + " order  by datetime(" + HandbookContract.NotificationEntry.COLUMN_TIMESTAMP + ") DESC ";
        int fetchedCount=0;
        Cursor cursor = sqliteDatabase.rawQuery(query_to_fetch_earliest, null);
        try {
            while (cursor.moveToNext() && fetchedCount < count){
                DiaryNote currentNote = new DiaryNote();
                currentNote.setDate(cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_DATE)));
                currentNote.setTitle(cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_TITLE)));
                currentNote.setDetail(cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_DETAIL)));
                currentNote.setImage_url(cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_IMAGE)));
                diaryNotes.add(currentNote);
                fetchedCount++;

            }
        }
        finally {
            cursor.close();
        }
        return diaryNotes;
    }

    public static Calendar getDatePart(Date date){
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }

    public static long daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        long daysBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    public static boolean isInDateRange(int days, String messageDate)
    {
        boolean inDateRange=true;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date hwDate = df.parse(messageDate);
            Date currentDate = new Date();
            return daysBetween(hwDate,currentDate) < days;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return true;
    }

    public static List<DiaryNote> loadLatestHomework(SQLiteDatabase sqliteDatabase, int type, String profileId, int count) {

        List<DiaryNote> diaryNotes = new ArrayList<>();
        String query_to_fetch_earliest="select *  from "+HandbookContract.NotificationEntry.TABLE_NAME+" where "+
                NotificationEntry.COLUMN_MSG_TYPE + " = '" + type + "' and " + NotificationEntry.COLUMN_TO_IDS +
                " LIKE " + "'%" + profileId + "%'" + " order  by datetime(" + HandbookContract.NotificationEntry.COLUMN_TIMESTAMP + ") DESC ";
        int fetchedCount=0;

        Cursor cursor = sqliteDatabase.rawQuery(query_to_fetch_earliest, null);
        try {
            while (cursor.moveToNext() && fetchedCount < count){

                String messageDate= cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_DATE));
                if(isInDateRange(3,messageDate)) {
                    DiaryNote currentNote = new DiaryNote();
                    currentNote.setDate(cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_DATE)));
                    currentNote.setTitle(cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_TITLE)));
                    currentNote.setDetail(cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_DETAIL)));
                    currentNote.setImage_url(cursor.getString(cursor.getColumnIndex(NotificationEntry.COLUMN_IMAGE)));
                    diaryNotes.add(currentNote);
                    fetchedCount++;
                }

            }
        }
        finally {
            cursor.close();
        }
        if(diaryNotes.size()==0)
        {
            //Insert no homework message
            Date currentDate = new Date();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            DiaryNote currentNote = new DiaryNote();
            currentNote.setDate(df.format(currentDate));
            currentNote.setTitle("No homework");
            currentNote.setDetail("");
            diaryNotes.add(currentNote);

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

    //function to update RoleProfile table like ImageURL of a profile
    public static void updateProfile(SQLiteDatabase sqliteDatabase, String profileImageUrl, String profileId) {

        // RoleProfile roleProfile = new RoleProfile();
        String query_to_fetch_earliest = "UPDATE " + HandbookContract.ProfileEntry.TABLE_NAME + " SET "
                + ProfileEntry.COLUMN_IMAGE + " = '" + profileImageUrl +
                "' where " + HandbookContract.ProfileEntry.COLUMN_ID + " = '" + profileId + "' ";


        try {
            sqliteDatabase.execSQL(query_to_fetch_earliest);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + NotificationEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                NotificationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                NotificationEntry.COLUMN_NOTIFICATION_ID + " INTEGER NOT NULL, " +
                NotificationEntry.COLUMN_PRIORITY + " INTEGER NOT NULL, " +
                NotificationEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                NotificationEntry.COLUMN_DETAIL + " TEXT NOT NULL, " +
                NotificationEntry.COLUMN_TITLE + " TEXT NOT NULL," +

                NotificationEntry.COLUMN_FROM + " TEXT NOT NULL," +
                NotificationEntry.COLUMN_IMAGE + " TEXT," +
                NotificationEntry.COLUMN_MSG_TYPE + " INTEGER, " +
                NotificationEntry.COLUMN_TO_IDS + " TEXT, " +
                NotificationEntry.COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + " );";

        final String SQL_CREATE_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS " + ProfileEntry.TABLE_NAME + " (" +
                ProfileEntry.COLUMN_ID + " TEXT NOT NULL," +
                ProfileEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_MIDDLE_NAME + " TEXT, " +
                ProfileEntry.COLUMN_LAST_NAME + " TEXT, " +
                HandbookContract.ProfileEntry.COLUMN_ROLE + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_GENDER + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_STD + " TEXT , " +
                HandbookContract.ProfileEntry.COLUMN_DOB + " INTEGER NOT NULL, " +
                NotificationEntry.COLUMN_IMAGE + " TEXT," +
                ProfileEntry.COLUMN_ADDRESS + " TEXT" + " );";


        final String SQL_CREATE_TIMETABLE_TABLE = "CREATE TABLE IF NOT EXISTS " + HandbookContract.TimetableEntry.TABLE_NAME + " (" +
                HandbookContract.TimetableEntry.COLUMN_ID + " TEXT NOT NULL," +
                HandbookContract.TimetableEntry.COLUMN_STD + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_SCHOOL_ID + " TEXT, " +
                HandbookContract.TimetableEntry.COLUMN_DAY + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_START_TIME + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_END_TIME + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_SUBJECT + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_TEACHER_NAME + " TEXT NOT NULL, " +
                HandbookContract.TimetableEntry.COLUMN_TEACHER_ID + " TEXT" + " );";

        final String SQL_CREATE_TEACHER_TABLE = "CREATE TABLE IF NOT EXISTS " + HandbookContract.TeacherForStudentEntry.TABLE_NAME + " (" +
                HandbookContract.TeacherForStudentEntry.COLUMN_STUDENTID + " TEXT NOT NULL, " +
                HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_ID+ " TEXT NOT NULL, "  +
                HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_FIRST_NAME + " TEXT NOT NULL, " +
                HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_LAST_NAME + " TEXT, " +
                HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_EMAIL + " TEXT, " +
                HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_MOBILE + " TEXT, " +
                HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_ROLE_STD + " TEXT, " +
                HandbookContract.TeacherForStudentEntry.COLUMN_TEACHER_SUBJECT + " TEXT  " +" );";


        final String SQL_CREATE_CONTACT_SCHOOL_TABLE = "CREATE TABLE IF NOT EXISTS " + HandbookContract.ContactSchoolEntry.TABLE_NAME + " (" +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ID + " TEXT NOT NULL, " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_NAME + " TEXT NOT NULL, " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_1 + " TEXT , " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_2 + " TEXT , " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_ADDRESS_3 + " TEXT , " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_CONTACT_NUMBER_1 + " TEXT , " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_CONTACT_NUMBER_2 + " TEXT , " +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_EMAIL_ID + " TEXT," +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_WEBSITE + " TEXT," +
                HandbookContract.ContactSchoolEntry.COLUMN_SCHOOL_LOGO + " BLOB);";

        final String SQL_CREATE_CALENDER_EVENTS = "CREATE TABLE IF NOT EXISTS " + HandbookContract.CalenderEventsEntry.TABLE_NAME + " (" +

                HandbookContract.CalenderEventsEntry.COLUMN_EVENT_ID + " TEXT , " +
                HandbookContract.CalenderEventsEntry.COLUMN_SCHOOL_ID + " TEXT , " +
                HandbookContract.CalenderEventsEntry.COLUMN_EVENT_NAME + " TEXT NOT NULL , " +
                HandbookContract.CalenderEventsEntry.COLUMN_EVENT_LOCATION + " TEXT NOT NULL , " +
                HandbookContract.CalenderEventsEntry.COLUMN_EVENT_DATE + " TEXT NOT NULL , " +
                HandbookContract.CalenderEventsEntry.COLUMN_EVENT_START_TIME + " TEXT NOT NULL , " +
                HandbookContract.CalenderEventsEntry.COLUMN_EVENT_END_TIME + " TEXT NOT NULL , " +
                HandbookContract.CalenderEventsEntry.COLUMN_EVENT_LIKE_BUTTON_CLICKED + " TEXT , " +
                HandbookContract.CalenderEventsEntry.COLUMN_EVENT_ADD_TO_CALENDER + " TEXT , " +
                HandbookContract.CalenderEventsEntry.COLUMN_STUDENT_ID + " TEXT , " +
                HandbookContract.CalenderEventsEntry.COLUMN_TEACHER_ID + " TEXT " + " );";


        final String SQL_CREATE_SCHOOL_HOLIDAY_TABLE = "CREATE TABLE IF NOT EXISTS " + HandbookContract.HolidayListsEntry.TABLE_NAME + " (" +
                HandbookContract.HolidayListsEntry.COLUMN_SCHOOL_ID + " TEXT NOT NULL, " +
                HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_ID + " TEXT, " +
                HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_NAME + " TEXT, " +
                HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_DESCRIPTION + " TEXT, " +
                HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_DATE + " TEXT , " +
                HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_MONTH + " TEXT , " +
                HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_YEAR + " TEXT , " +
                HandbookContract.HolidayListsEntry.COLUMN_HOLIDAY_TYPE + " TEXT );";


        sqLiteDatabase.execSQL(SQL_CREATE_NOTIFICATIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PROFILE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TIMETABLE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CONTACT_SCHOOL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CALENDER_EVENTS);
        sqLiteDatabase.execSQL(SQL_CREATE_TEACHER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SCHOOL_HOLIDAY_TABLE);
        //HandBookDbHelper.insertNotification(sqLiteDatabase, "Welcome to SchoolLink", "SchoolLink is the app through which you will receive update from school", new Date().toString(), 1, "SchoolLink", 10001,"",101,"110,105");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME);
        onCreate((sqLiteDatabase));

    }



}
