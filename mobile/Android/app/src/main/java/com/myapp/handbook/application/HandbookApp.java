package com.myapp.handbook.application;

import android.app.Application;
import android.util.Log;

import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.listener.FetchListener;

/**
 * Created by user on 9/17/2017.
 */

public class HandbookApp extends Application {
    private Fetch fetch;

    @Override
    public void onCreate() {
        super.onCreate();

        //Set settings for Fetch
        new Fetch.Settings(this)
                .setAllowedNetwork(Fetch.NETWORK_ALL)
                .enableLogging(true)
                .setConcurrentDownloadsLimit(1)
                .apply();

        fetch = Fetch.newInstance(this);

        fetch.addFetchListener(new FetchListener() {
            @Override
            public void onUpdate(long id, int status, int progress, long downloadedBytes, long fileSize, int error) {

                Log.d("fetchDebug","id:" + id + ",status:" + status + ",progress:" + progress
                        + ",error:" + error);

                Log.i("fetchDebug","id: " + id + " downloadedBytes: " + downloadedBytes + " / fileSize: " + fileSize);
            }
        });


    }

    public Fetch getFetch() {
        return fetch;
    }

}
