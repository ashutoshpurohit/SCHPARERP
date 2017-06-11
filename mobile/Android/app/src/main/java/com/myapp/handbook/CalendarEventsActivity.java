package com.myapp.handbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.myapp.handbook.Listeners.RecycleViewClickListener;
import com.myapp.handbook.Tasks.FetchSchoolCalendarAsyncTask;
import com.myapp.handbook.adapter.SchoolCalendarAdapter;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarEventsActivity extends AppCompatActivity implements RecycleViewClickListener{

    List<Event> events;
    RecyclerView calendarView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        calendarView = (RecyclerView) findViewById(R.id.schoolCalendarView);
        calendarView.setHasFixedSize(true);

        LinearLayoutManager calendarLayoutManager = new LinearLayoutManager(this);
        calendarLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        calendarView.setLayoutManager(calendarLayoutManager);
        setSupportActionBar(toolbar);

        progressDialog = ProgressDialog.show(this, "Downloading calendar", "Please wait", false);
        FetchSchoolCalendarAsyncTask.CalendarDownloadedListener getEvents= new FetchSchoolCalendarAsyncTask.CalendarDownloadedListener() {
            @Override
            public void onFinished(List<Event> currentEvents) {
                events=currentEvents;
            }
        };

        FetchSchoolCalendarAsyncTask.CalendarDownloadedListener saveEventsToDB= new FetchSchoolCalendarAsyncTask.CalendarDownloadedListener() {
            @Override
            public void onFinished(List<Event> currentEvents) {
                HandBookDbHelper.saveSchoolCalendarToDB(currentEvents);
            }
        };


        FetchSchoolCalendarAsyncTask.CalendarDownloadedListener setupView= new FetchSchoolCalendarAsyncTask.CalendarDownloadedListener() {
            @Override
            public void onFinished(List<Event> currentEvents) {
                setupSchoolCalendarView(currentEvents);
            }
        };

        FetchSchoolCalendarAsyncTask.CalendarDownloadedListener clearBusyDialog= new FetchSchoolCalendarAsyncTask.CalendarDownloadedListener() {
            @Override
            public void onFinished(List<Event> events) {
                progressDialog.dismiss();
            }
        };

        List<FetchSchoolCalendarAsyncTask.CalendarDownloadedListener> listeners= new ArrayList<>();
        listeners.add(getEvents);
        listeners.add(setupView);
        listeners.add(clearBusyDialog);

        FetchSchoolCalendarAsyncTask task = new FetchSchoolCalendarAsyncTask(listeners);
        task.execute();

        //setupSchoolCalendarView(currentEvents);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        toolbar.setTitle("School Calendar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupSchoolCalendarView(List<Event> currentEvents) {

        if(currentEvents!=null){
            SchoolCalendarAdapter adapter = new SchoolCalendarAdapter(this,currentEvents, HttpConnectionUtil.ViewType.DETAIL,this);
            calendarView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void recyclerViewClicked(View v, int position) {
        switch (v.getId()) {
            case R.id.event_LikeButton:
                ImageButton likeButton = (ImageButton) v;
                v.setBackgroundResource(R.drawable.ic_favorite_fill);
                Toast.makeText(this, "Like pressed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.event_AddToCalendarButton:
                //addToCalendar();
                Toast.makeText(this, "Added to your calendar", Toast.LENGTH_SHORT).show();
                addToPhoneCalendar(position);
                break;
            default:
                break;
        }
    }

    private void addToPhoneCalendar(int position) {

        try {
            Event event = events.get(position);
            if (event != null) {
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(2017, 0, 24, 7, 30);
                Calendar endTime = Calendar.getInstance();
                endTime.set(2012, 0, 24, 8, 30);
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, event.getEventName())
                        .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getEventPlace())
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                startActivity(intent);
            }
        }
        catch(Exception ex){
            Toast.makeText(this,"Failed to add event to the calendar",Toast.LENGTH_SHORT);
        }
    }

}
