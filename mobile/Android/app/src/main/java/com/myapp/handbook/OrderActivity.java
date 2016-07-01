package com.myapp.handbook;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class OrderActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


}
