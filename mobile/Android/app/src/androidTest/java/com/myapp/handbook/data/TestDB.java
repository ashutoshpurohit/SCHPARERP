package com.myapp.handbook.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.myapp.handbook.HttpConnectionUtil;

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
        //deleteTheDatabase();
    }



    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    @SmallTest
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

    @SmallTest
    public void testInsertToNotificationsTable() throws Throwable{
        SQLiteDatabase db = new HandBookDbHelper(
                this.mContext).getWritableDatabase();

        String title="Holiday tomorrow";
        HandBookDbHelper.insertNotification(db, title, "Holiday on 23 March 2016 on occasion of Holi", new Date().toString(), 1, "Admin", 10001,"http://floating-bastion-86283.herokuapp.com/uploadTeacherOrStudentImage/IMG_11 Sep 2016 6:50:47 am.jpg", HttpConnectionUtil.HOMEWORK_TYPE,"110, 105");
        Cursor cursor = db.query("notifications", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            Assert.assertEquals(title,cursor.getString(5));
            Assert.assertNotNull(cursor.getString(7));
        }
        cursor.close();
        db.close();


    }

    @SmallTest
    public void testInsertToSchoolContactEntryTable() throws Throwable{
        SQLiteDatabase db = new HandBookDbHelper(
                this.mContext).getWritableDatabase();

        String title="Holiday tomorrow";
        HandBookDbHelper.insertSchoolContactEntry("100","Test School Name","Test Address1","Test Address2",
                "Test Address3","13445555","5555555","website.com","email@email.com",null );

        Cursor cursor = db.query(HandbookContract.ContactSchoolEntry.TABLE_NAME, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            Assert.assertEquals(title,cursor.getString(5));
            Assert.assertNotNull(cursor.getString(7));
        }
        cursor.close();
        db.close();


    }


   /* @SmallTest
    public void testOrderByQueryNotificationsTable() throws Throwable{
        SQLiteDatabase db = new HandBookDbHelper(
                this.mContext).getWritableDatabase();

        String title1="Holiday tomorrow";
        String title2="Test";
        HandBookDbHelper.insertNotification(db, title1, "Holiday on 23 March 2016 on occasion of Holi", new Date().toString(), 1, "Admin", 10001,null);
        HandBookDbHelper.insertNotification(db,title2 , "This is wonderful day", new Date().toString(), 2, "Admin", 10002,null);
        String query_to_fetch_earliest="select *  from "+HandbookContract.NotificationEntry.TABLE_NAME+" order  by datetime("+HandbookContract.NotificationEntry.COLUMN_TIMESTAMP+") ASC ";

        Cursor cursor = db.rawQuery(query_to_fetch_earliest, null);
        //Cursor cursor = db.query("notifications", null, null, null, null, null, "DATETIME("+HandbookContract.NotificationEntry.COLUMN_TIMESTAMP+")"+" DESC");
        if(cursor.moveToFirst()){
            //Assert.assertEquals(title2,cursor.getString(5));
            //Assert.assertNotNull(cursor.getString(7));
            System.out.println(cursor.getString(5)+ ":"+cursor.getString(7));
            cursor.moveToNext();
            System.out.println(cursor.getString(5)+ ":"+cursor.getString(7));
        }
        cursor.close();
        db.close();


    }
*/
    public void testSearchQueryNotificationsTable() throws Throwable{
        SQLiteDatabase db = new HandBookDbHelper(
                this.mContext).getWritableDatabase();

        String title1="Holiday tomorrow";
        String title2="Test";
        //HandBookDbHelper.insertNotification(db, title1, "Holiday on 23 March 2016 on occasion of Holi", new Date().toString(), 1, "Admin", 10001);
        //HandBookDbHelper.insertNotification(db,title2 , "This is wonderful day", new Date().toString(), 2, "Admin", 10002);
        //String query_to_fetch_earliest="select *  from "+HandbookContract.NotificationEntry.TABLE_NAME+" order  by datetime("+HandbookContract.NotificationEntry.COLUMN_TIMESTAMP+") ASC ";\
        String query ="Holiday";

        String defQuery ="SELECT *  FROM "+ HandbookContract.NotificationEntry.TABLE_NAME;
        String searchQuery = "SELECT *  FROM "+ HandbookContract.NotificationEntry.TABLE_NAME + " where " + HandbookContract.NotificationEntry.COLUMN_DETAIL +" like \'%"+ query + "%\'";// order  by datetime(" + HandbookContract.NotificationEntry.COLUMN_TIMESTAMP+") DESC ";

        Cursor cursor = db.rawQuery(searchQuery, null);
        Cursor totalcursor = db.rawQuery(defQuery, null);

        int totalCount=totalcursor.getCount();
        int filterCount = cursor.getCount();

        //Cursor cursor = db.query("notifications", null, null, null, null, null, "DATETIME("+HandbookContract.NotificationEntry.COLUMN_TIMESTAMP+")"+" DESC");
        Assert.assertEquals(3,filterCount);
        cursor.close();
        db.close();


    }


    public void testInsertToProfileTable() throws Throwable{
        SQLiteDatabase db = new HandBookDbHelper(
                this.mContext).getWritableDatabase();

        String firstname="Ashutosh";
        HandBookDbHelper.insertProfile(db,"001","Ashutosh", "Solanki", "Avinash", "Student", "M","12","69 Chandralok Indore",new Date(1978,9,20).toString(),"");
        Cursor cursor = db.query("profile", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            Assert.assertEquals(firstname,cursor.getString(1));

        }
        cursor.close();
        db.close();


    }

}
