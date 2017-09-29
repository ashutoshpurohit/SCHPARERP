package com.myapp.handbook.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by user on 9/29/2017.
 */

public class AndroidPermissions {

    public static int REQUEST_STORAGE = 1;
    public static boolean hasStoragePermissionGranted(Context context){
        return  ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestExternalStoragePermission(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            String [] permissions= {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(activity,permissions ,
                    REQUEST_STORAGE);
        }
    }

}
