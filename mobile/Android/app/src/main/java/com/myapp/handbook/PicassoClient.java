package com.myapp.handbook;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by SAshutosh on 9/19/2016.
 */
public class PicassoClient extends Application {

        @Override
        public void onCreate() {
            super.onCreate();

            Picasso.Builder builder = new Picasso.Builder(this);
            builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
            Picasso built = builder.build();
            built.setIndicatorsEnabled(true);
            built.setLoggingEnabled(true);
            Picasso.setSingletonInstance(built);

        }

        @Override
        protected void attachBaseContext(Context base) {
            super.attachBaseContext(base);
            MultiDex.install(base);
        }
}
