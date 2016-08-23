package com.myapp.handbook;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.myapp.handbook.data.HandBookDbHelper;

/**
 * Created by sashutosh on 6/27/2016.
 */
public class GcmIntentService extends IntentService {

    public static int id=0;
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

            processMessage(extras);
            sendNotification(extras.getString("title"), extras.getString("body"));
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void processMessage(Bundle data) {

        SQLiteOpenHelper notificationHelper = new HandBookDbHelper(this);
        Notifications.setDb(notificationHelper.getWritableDatabase());
        String msgType =data.getString("type");
        String title = data.getString("title");
        String message = data.getString("body");
        //if ((msgType.equalsIgnoreCase(getResources().getString(R.string.app_notice))))
        Notifications.notify(data);
        /*else if( (msgType.equalsIgnoreCase(getResources().getString(R.string.app_assignment)))){

            Assignment asgmt = new Assignment();
            asgmt.assignment= message;
            Assignments.addAssignment(asgmt);
        }*/


    }

    private void sendNotification(String title,String msg) {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("requestType",HttpConnectionUtil.GCM_NOTIFICATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
               intent , 0);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(id, mBuilder.build());
        id++;
    }
}
