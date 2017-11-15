package com.myapp.handbook.data;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by SAshutosh on 3/22/2016.
 */
public class HandbookContract {
    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }



    /* Inner class that defines the contents of the weather table */
    public static final class NotificationEntry implements BaseColumns {

        public static final String TABLE_NAME = "notifications";

        // Column with the foreign key into the location table.
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_NOTIFICATION_ID = "notification_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_DETAIL = "detail";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_FROM = "msg_source";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_TIMESTAMP ="timestamp";
        public static final String COLUMN_TO_IDS="msg_to_ids";
        public static final String COLUMN_MSG_TYPE= "msg_type";


        }

    public static final class ProfileEntry implements BaseColumns {

        public static final String TABLE_NAME = "profile";

        // Column with the foreign key into the location table.
        public static final String COLUMN_ID = "id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_FIRST_NAME = "first_name";

        public static final String COLUMN_MIDDLE_NAME = "middle_name";
        public static final String COLUMN_LAST_NAME = "last_name";

        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_ROLE = "role";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_GENDER = "gender";

        public static final String COLUMN_DOB = "birth_date";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_STD = "class";
        public static final String COLUMN_ADDRESS = "address";

        public  static final String COLUMN_IMAGE ="image";

    }

    public static final class TimetableEntry implements BaseColumns {

        public static final String TABLE_NAME = "timetable";

        // Column with the foreign key into the location table.
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_STD = "class";
        public static final String COLUMN_SCHOOL_ID = "school_id";
        public static final String COLUMN_DAY = "day_of_week";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_SUBJECT = "subject";
        public static final String COLUMN_TEACHER_NAME = "teacher_name";
        public static final String COLUMN_TEACHER_ID = "teacher_id";

    }

    public static final class TeacherForStudentEntry implements BaseColumns {

        public static final String TABLE_NAME = "teachers";

        // Column with the foreign key into the location table.

        public static final String COLUMN_STUDENTID = "student_id";
        public static final String COLUMN_TEACHER_FIRST_NAME ="teacher_first_name";
        public static final String COLUMN_TEACHER_ID ="teacherId";
        public static final String COLUMN_TEACHER_LAST_NAME ="teacher_last_name";
        public static final String COLUMN_TEACHER_MOBILE ="mobileNumber";
        public static final String COLUMN_TEACHER_EMAIL ="emailId";
        public static final String COLUMN_TEACHER_ROLE_STD ="roleforStd";
        public static final String COLUMN_TEACHER_SUBJECT ="roleforSubject";


    }


    //Table for School Contact Details
    public static final class ContactSchoolEntry implements BaseColumns {

        public static final String TABLE_NAME = "schoolcontacts";

        public static final String COLUMN_SCHOOL_ID = "school_id";
        public static final String COLUMN_SCHOOL_NAME = "school_name";
        public static final String COLUMN_SCHOOL_ADDRESS_1 = "school_address_1";
        public static final String COLUMN_SCHOOL_ADDRESS_2 = "school_address_2";
        public static final String COLUMN_SCHOOL_ADDRESS_3 = "school_address_3";
        public static final String COLUMN_SCHOOL_CONTACT_NUMBER_1 = "contact_number_1";
        public static final String COLUMN_SCHOOL_CONTACT_NUMBER_2 = "contact_number_2";
        public static final String COLUMN_SCHOOL_EMAIL_ID = "school_email_id";
        public static final String COLUMN_SCHOOL_WEBSITE = "school_website";
        public static final String COLUMN_SCHOOL_LOGO = "school_logo";


    }

    //Table for Calender events entries
    public static final class CalenderEventsEntry implements BaseColumns {

        public static final String TABLE_NAME = "calenderevents";

        public static final String COLUMN_SCHOOL_ID = "school_id";
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_EVENT_NAME = "event_name";
        public static final String COLUMN_EVENT_LOCATION = "event_location";
        public static final String COLUMN_EVENT_DATE = "event_date";
        public static final String COLUMN_EVENT_START_TIME= "event_start_time";
        public static final String COLUMN_EVENT_END_TIME= "event_end_time";
        public static final String COLUMN_EVENT_LIKE_BUTTON_CLICKED = "like_button_clicked";
        public static final String COLUMN_EVENT_ADD_TO_CALENDER = "add_to_calender";

        //below 2 columns are lists and they need to be associated with single event id in seperate table
        //temperory its stored in this table for assuming single student and single teacher
        public static final String COLUMN_STUDENT_ID = "student_id";
        public static final String COLUMN_TEACHER_ID = "teacher_id";

    }

    //Table for Calender events entries
    public static final class HolidayListsEntry implements BaseColumns {

        public static final String TABLE_NAME = "holidaylists";

        public static final String COLUMN_SCHOOL_ID = "school_id";
        public static final String COLUMN_HOLIDAY_ID = "holiday_id";
        public static final String COLUMN_HOLIDAY_NAME = "holiday_name";
        public static final String COLUMN_HOLIDAY_DESCRIPTION = "holiday_description";
        public static final String COLUMN_HOLIDAY_DATE = "holiday_date";
        public static final String COLUMN_HOLIDAY_MONTH = "holiday_month";
        public static final String COLUMN_HOLIDAY_YEAR = "holiday_year";
        public static final String COLUMN_HOLIDAY_TYPE = "holiday_type";

    }


}
