package com.myapp.handbook.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;


/**
 * Created by user on 9/17/2017.
 */

public class HandbookApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();



    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
