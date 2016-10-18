package com.myapp.handbook;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import com.myapp.handbook.adapter.TimeTableAdapter;
import com.myapp.handbook.controls.DatePickerFragment;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.TimeSlots;
import com.myapp.handbook.domain.TimeTable;
import com.myapp.handbook.domain.WeeklyTimeTable;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

public class TimeTableActivity extends AppCompatActivity implements View.OnClickListener {

    TimeTable profileTimeTable;
    SQLiteDatabase db;
    ListView timeTableListView;
    View headerView;
    Button datePickerButton;
    Date selectedDate;
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

        new FetchTimeTableAsyncTask().execute();
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
                SetupView();
            }
        });
        dialog.show(getSupportFragmentManager(),"Select date");

    }

    private class FetchTimeTableAsyncTask extends AsyncTask<Void, Void, TimeTable> {
        @Override
        protected TimeTable doInBackground(Void... params) {
            HttpConnectionUtil.TimeTableService timeTableService = ServiceGenerator.createService(HttpConnectionUtil.TimeTableService.class);
            //Get the id of teacher or student
            List<RoleProfile> profiles = HandBookDbHelper.LoadProfilefromDb(db);
            //TO-DO Hardcoded to 0th indexed need to be changed based on currently selected profile
            String id = profiles.get(0).getId();
            //String id ="100";
            Call<TimeTable> call = timeTableService.getStudentTimeTable(id);
            try {
                TimeTable timeTable = call.execute().body();
                return timeTable;
            } catch (IOException e) {
                Log.d("SchoolContact", "Error in fetching school profile");
            }

            return null;
        }

        @Override
        protected void onPostExecute(TimeTable timeTable) {
            profileTimeTable =timeTable;
            SetupView();
        }

    }

    private void SetupView() {

        if(profileTimeTable!=null){

            //setButtonText();
            int dayOfWeek = getDayOfTheWeek();
            List<WeeklyTimeTable> weekly= profileTimeTable.getWeeklyTimeTableList();
            List<TimeSlots> todaysTimeSlot=null;
            View view =findViewById(R.id.empty_list_view);
            if(dayOfWeek > -1) {
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
        return day -2;
    }
}
