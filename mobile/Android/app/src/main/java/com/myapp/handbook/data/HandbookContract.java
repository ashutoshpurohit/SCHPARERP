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

    }


}
