package com.myapp.handbook;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.Event;
import com.myapp.handbook.domain.MsgType;

import java.util.List;

import static com.myapp.handbook.HttpConnectionUtil.sharedPreferences;

/**
 * Created by sashutosh on 6/27/2016.
 */
public class GcmIntentService extends IntentService {

    public static int id=0;
    SQLiteOpenHelper notificationHelper;// = new HandBookDbHelper(this);
    SQLiteDatabase db;
    List<Event> newEvents;

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

        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void processMessage(Bundle data) {


        notificationHelper = new HandBookDbHelper(this);
        db = notificationHelper.getWritableDatabase();
        Notifications.setDb(db);
        String msgType =data.getString("type");
        String title = data.getString("title");
        String message = data.getString("body");
        String ToIds = data.getString("ToIds");

        if(sharedPreferences==null)
        {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }

        if (((msgType != null ? msgType.equalsIgnoreCase(MsgType.HOMEWORK.toString()) : false) || msgType.equalsIgnoreCase(MsgType.DIARY_NOTE.toString()) || msgType.equalsIgnoreCase(MsgType.PARENT_NOTE.toString()))) {
            Notifications.notify(data);
            sendNotification(title, message,HttpConnectionUtil.GCM_NOTIFICATION);

        }
        else if((msgType.equalsIgnoreCase(MsgType.TIMETABLE.toString()))){
            processTimetableChange();

        }
        else if(msgType.equalsIgnoreCase(MsgType.EVENTUPDATE.toString())){
            processEventChange();
            sendNotification("School calendar updated", "School calendar has been modified. Please check the school calendar page", HttpConnectionUtil.EVENT_NOTIFICATION);
        }

    }

    private void processEventChange() {
        //Reset the flag that events have been downloaded
        sharedPreferences.edit().putBoolean(QuickstartPreferences.SCHOOL_CALENDER_EVENTS_DOWNLOADED, false).commit();

        //Clear the current events
        HandBookDbHelper.clearAllSchoolEvents(db);

    }

    private void processTimetableChange(){

    }


    private void sendNotification(String title,String msg, int type) {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("requestType",type);
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
