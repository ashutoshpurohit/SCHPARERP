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

        if (sharedPreferences.getBoolean(QuickstartPreferences.TIMETABLE_DOWNLOADED+"_"+selectedProfile.getId(), false) == false) {
            new FetchTimeTableAsyncTask(selectedProfile).execute();
        }
        else
        {
            profileTimeTable = HandBookDbHelper.loadStudentTimeTable(db,selectedProfileId);

        }
        SetupView();

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

        RoleProfile currentProfile;


        public FetchTimeTableAsyncTask(RoleProfile profile) {
            this.currentProfile =profile;
        }

        @Override
        protected TimeTable doInBackground(Void... params) {
            HttpConnectionUtil.TimeTableService timeTableService = ServiceGenerator.createService(HttpConnectionUtil.TimeTableService.class);

            Call<TimeTable> call=null;
            if(currentProfile.getRole() == RoleProfile.ProfileRole.STUDENT.toString())//String id ="100";
            {
                call = timeTableService.getStudentTimeTable(currentProfile.getId());
            }
            else
            {
                call = timeTableService.getTeacherTimeTable(currentProfile.getId());
            }
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
            if(timeTable!=null) {
                boolean result =false;
                if(currentProfile.getRole()== RoleProfile.ProfileRole.STUDENT.toString()) {
                    result =saveStudentTimeTable(currentProfile.getId(), profileTimeTable);
                }
                else if(currentProfile.getRole()==RoleProfile.ProfileRole.TEACHER.toString()){
                    result = saveTeacherTimeTable(currentProfile.getId(),profileTimeTable);
                }
                if(result)
                {
                    sharedPreferences.edit().putBoolean(QuickstartPreferences.TIMETABLE_DOWNLOADED+"_" +currentProfile.getId() , true).commit();
                }
            }
        }

    }

    private boolean saveStudentTimeTable(String id, TimeTable profileTimeTable) {

        boolean success=true;
        String school_id = profileTimeTable.getSchoolId();
        String std = profileTimeTable.getStudentClassStandard();
        for(WeeklyTimeTable day: profileTimeTable.getWeeklyTimeTableList()){
            String dayOfWeek = day.getDayOfWeek();
            for(TimeSlots timeSlot: day.getTimeSlotsList()){
                long row_id =HandBookDbHelper.insertTimeTableEntry(db,id,dayOfWeek,school_id,std,timeSlot.getTeacherId(),timeSlot.getTeacherName(),timeSlot.getStartTime(),timeSlot.getEndTime(),timeSlot.getSubject());
                if(row_id<0)
                    success =false;
            }
        }
        return success;
    }

    private boolean saveTeacherTimeTable(String id, TimeTable profileTimeTable) {

        boolean success=true;
        String school_id = profileTimeTable.getSchoolId();

        for(WeeklyTimeTable day: profileTimeTable.getWeeklyTimeTableList()){
            String dayOfWeek = day.getDayOfWeek();
            for(TimeSlots timeSlot: day.getTimeSlotsList()){
                long row_id =HandBookDbHelper.insertTimeTableEntry(db,id,dayOfWeek,school_id,timeSlot.getTeacherClassStd(),timeSlot.getTeacherId(),timeSlot.getTeacherName(),timeSlot.getStartTime(),timeSlot.getEndTime(),timeSlot.getSubject());
                if(row_id<0)
                    success =false;
            }
        }
        return success;
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
        return day;
    }
}
