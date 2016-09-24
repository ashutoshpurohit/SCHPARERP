package com.myapp.handbook;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.myapp.handbook.data.HandBookDbHelper;

import java.util.ArrayList;

/**
 * Created by SAshutosh on 3/11/2016.
 */
public class Notifications {

    public static ArrayList<String> notes =new ArrayList<>();

    public static SQLiteDatabase handbookDB;

    public static void addNote(String note){
        notes.add(note);
    }

    public static void setDb(SQLiteDatabase db){
        handbookDB =db;
    }

    public static void notify(Bundle data){

        String msgType =data.getString("type");
        String title = data.getString("title");
        String message = data.getString("body");
        String date = data.getString("date");
        String image = data.getString("ImageUrl");
        int note_id = Integer.parseInt(data.getString("notification_id"));
        int priority = Integer.parseInt(data.getString("priority"));
        HandBookDbHelper.insertNotification(handbookDB,title,message,date,priority,"",note_id,image);

    }

    public static void load(){

    }

}
