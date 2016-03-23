package com.myapp.handbook.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.data.HandbookContract;

import junit.framework.Assert;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by SAshutosh on 3/22/2016.
 */
public class TestDB extends AndroidTestCase {
    public static final String LOG_TAG = TestDB.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(HandBookDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }



    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();

        tableNameHashSet.add(HandbookContract.NotificationEntry.TABLE_NAME);

        mContext.deleteDatabase(HandBookDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new HandBookDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());
    }

    public void testInsertToNotificationsTable() throws Throwable{
        SQLiteDatabase db = new HandBookDbHelper(
                this.mContext).getWritableDatabase();

        String title="Holiday Notification";
        HandBookDbHelper.insertNotification(db,title,"Holiday on 23 March 2016 on occasion of Holi",new Date().toString(),1,"Admin",10001);
        Cursor cursor = db.query("notifications", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            Assert.assertEquals(title,cursor.getString(5));

        }
        cursor.close();
        db.close();


    }
}
