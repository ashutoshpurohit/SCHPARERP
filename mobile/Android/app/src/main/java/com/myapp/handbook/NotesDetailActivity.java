package com.myapp.handbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import static com.myapp.handbook.NotesActivity.MESSAGE_TYPE;

public class NotesDetailActivity extends AppCompatActivity {
    int messageType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detail);
        NotesDetailFragment fragment = new NotesDetailFragment();
       // fragment.setArguments(arguments);
        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_child_toolbar);
        setSupportActionBar(myChildToolbar);

        Intent intent = getIntent();
        messageType = intent.getIntExtra(MESSAGE_TYPE, 1);


        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.notes_detail_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();



    }

    @Override
    public void onBackPressed() {

        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
       /* finish();
        Intent intent =null;
        if(messageType == HttpConnectionUtil.HOMEWORK_TYPE) {
           intent = new Intent(getApplicationContext(), NotesActivity.class);
            intent.putExtra(NotesActivity.MESSAGE_TYPE, HttpConnectionUtil.HOMEWORK_TYPE);
        }else if(messageType == HttpConnectionUtil.DIARY_NOTE_TYPE){
            intent = new Intent(getApplicationContext(), NotesActivity.class);
            intent.putExtra(NotesActivity.MESSAGE_TYPE, HttpConnectionUtil.DIARY_NOTE_TYPE);
        }
        startActivity(intent);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       /* //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home)
        {
            LaunchNotificationFragment();
        }*/
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


        //return super.onOptionsItemSelected(item);
    }


    private void LaunchNotificationFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        //Bundle extras = intent.getExtras();
        intent.putExtra("position", 1);
        startActivity(intent);
    }
}
