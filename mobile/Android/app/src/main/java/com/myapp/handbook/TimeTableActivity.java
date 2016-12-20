package com.myapp.handbook;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import com.myapp.handbook.Listeners.TimeTableDbUpdateListener;
import com.myapp.handbook.Tasks.FetchTimeTableAsyncTask;
import com.myapp.handbook.adapter.TimeTableAdapter;
import com.myapp.handbook.controls.DatePickerFragment;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.BaseTimeTable;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.TimeSlots;
import com.myapp.handbook.domain.TimeTable;
import com.myapp.handbook.domain.WeeklyTimeTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

public class TimeTableActivity extends AppCompatActivity implements View.OnClickListener {

    BaseTimeTable profileTimeTable;
    String selectedProfileId;
    RoleProfile selectedProfile;
    ListView timeTableListView;
    View headerView;
    Button datePickerButton;
    Date selectedDate;
    private SharedPreferences sharedPreferences;
    List<RoleProfile> profiles;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        ab.setTitle("Timetable");

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(this);

        db = handbookDbHelper.getReadableDatabase();

        //headerView =getLayoutInflater().inflate(R.layout.listview_timetable_header,null);
        timeTableListView = (ListView) findViewById(R.id.timeTableListView);
        //timeTableListView.addHeaderView(headerView);
        timeTableListView.setEmptyView(findViewById(R.id.empty_list_view));

        datePickerButton =(Button)findViewById(R.id.datepickerButton);

        datePickerButton.setOnClickListener(this);

        selectedDate = new Date();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //TO-DO read the value of selectedProfileId as per app logic
        //Get the id of teacher or student

        //selectedProfileId =profiles.get(0).getId();
        selectedProfile = RoleProfile.getProfile(db,HttpConnectionUtil.getSelectedProfileId());

        FetchTimeTableAsyncTask.TaskListener uiUpdater = new FetchTimeTableAsyncTask.TaskListener() {
            @Override
            public void onFinished(BaseTimeTable table) {
                SetupView(table);
            }
        };

        FetchTimeTableAsyncTask.TaskListener dbUpdater = new TimeTableDbUpdateListener(db,selectedProfile,sharedPreferences);

        List<FetchTimeTableAsyncTask.TaskListener> listeners = new ArrayList<>();
        listeners.add(uiUpdater);
        listeners.add(dbUpdater);

        if (sharedPreferences.getBoolean(QuickstartPreferences.TIMETABLE_DOWNLOADED+"_"+selectedProfile.getId(), false) == false) {
            new FetchTimeTableAsyncTask(selectedProfile,listeners).execute();
        }
        else
        {
            profileTimeTable = HandBookDbHelper.loadTimeTable(db,selectedProfileId, selectedProfile.getProfileRole());

        }
        SetupView(null);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        DatePickerFragment dialog = new DatePickerFragment();
        dialog.setDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDate = new Date(year,monthOfYear,dayOfMonth);
                setButtonText();
                SetupView(profileTimeTable);
            }
        });
        dialog.show(getSupportFragmentManager(),"Select date");

    }

    private void SetupView(BaseTimeTable table) {

        profileTimeTable =table;

        if(profileTimeTable!=null){

            //setButtonText();
            int dayOfWeek = getDayOfTheWeek();
            List<WeeklyTimeTable> weekly= profileTimeTable.getWeeklyTimeTableList();
            List<TimeSlots> todaysTimeSlot=null;
            View view =findViewById(R.id.empty_list_view);
            if(dayOfWeek > -1 && dayOfWeek < weekly.size()) {
                todaysTimeSlot = weekly.get(dayOfWeek).getTimeSlotsList();
                TimeTableAdapter adapter = new TimeTableAdapter(this, R.layout.list_timetable_item, todaysTimeSlot);
                timeTableListView.setAdapter(adapter);
                view.setVisibility(View.INVISIBLE);
            }
            else {

                view.setVisibility(View.VISIBLE);

            }
        }
    }

    private void setButtonText() {

        Date todaysDate= new Date();
        if(compareDate(todaysDate,selectedDate))
            datePickerButton.setText(R.string.today);
        else
        {
            //Make Date String
            String dateString = getDateAsString(selectedDate);
            datePickerButton.setText(dateString);
        }

    }

    private String getDateAsString(Date selectedDate) {
        String dateString = (String) android.text.format.DateFormat.format("dd", selectedDate) + "/" + (String) android.text.format.DateFormat.format("MMM", selectedDate)+"/"+selectedDate.getYear();
        return dateString;
    }

    private boolean compareDate(Date todaysDate, Date otherDate) {

        return (otherDate.getDay()== todaysDate.getDay() && otherDate.getMonth() == todaysDate.getMonth() && otherDate.getYear()== todaysDate.getYear());

    }

    private int getDayOfTheWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedDate.getYear(),selectedDate.getMonth(),selectedDate.getDay());
        //calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        //Subtract a day to adjust for 0th index
        return day;
    }
}
