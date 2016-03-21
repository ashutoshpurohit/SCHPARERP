package com.myapp.handbook;

import java.util.ArrayList;

/**
 * Created by SAshutosh on 3/11/2016.
 */
public class Notifications {

    public static ArrayList<String> notes =new ArrayList<>();

    public static void addNote(String note){
        notes.add(note);
    }
}
