package com.myapp.handbook.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.myapp.handbook.HttpConnectionUtil;
import com.myapp.handbook.QuickstartPreferences;
import com.myapp.handbook.R;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

public class Login extends AppCompatActivity {
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "FYb2ipP3TtsjypFzFcqmUIgzR";
    private static final String TWITTER_SECRET = "t0vQHLYZp6PSAyBYnZB4a260SFFT20RChlE1xx8ixzkCaWGuA6";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new TwitterCore(authConfig), new Digits());
        Fabric.with(this, new Crashlytics(), new TwitterCore(authConfig), new Digits());

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // TODO: associate the session userID with your user model
                Toast.makeText(getApplicationContext(), "Authentication successful for "
                        + phoneNumber, Toast.LENGTH_LONG).show();

                if(phoneNumber!=null && phoneNumber.length() >= 10 ) {
                    //Remove country code
                    String finalNumber = filterCountryCode(phoneNumber);
                    HttpConnectionUtil.setMobilenumber(finalNumber);
                    //Set the Logged in flag to true
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sharedPreferences.edit().putBoolean(QuickstartPreferences.LOGGED_IN, true).apply();
                    sharedPreferences.edit().putString(QuickstartPreferences.LOGGED_MOBILE,finalNumber).apply();
                    Intent intent = new Intent(getBaseContext(),com.myapp.handbook.MainActivity.class);
                    startActivity(intent);
                }
                else{
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sharedPreferences.edit().putBoolean(QuickstartPreferences.LOGGED_IN, false).apply();
                }

            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });

    }

    private String filterCountryCode(String phoneNumber) {

        //Assuming 10 digit mobile number world wide
        return phoneNumber.substring(phoneNumber.length()-10);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result){
        super.onActivityResult(requestCode,resultCode,result);
    }

}
