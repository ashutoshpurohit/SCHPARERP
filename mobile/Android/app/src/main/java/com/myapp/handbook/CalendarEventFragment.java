package com.myapp.handbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.handbook.Listeners.RecycleViewClickListener;
import com.myapp.handbook.Tasks.FetchSchoolCalendarAsyncTask;
import com.myapp.handbook.adapter.SchoolCalendarAdapter;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.CalendarEvents;
import com.myapp.handbook.domain.Event;
import com.myapp.handbook.domain.SchoolProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ashutosh on 10/16/2017.
 */

public class CalendarEventFragment extends Fragment implements RecycleViewClickListener, AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<Event> events;
    RecyclerView calendarView;
    SQLiteDatabase db;
    SharedPreferences sharedPreferences;
    ArrayAdapter<CharSequence> monthAdapter;
    Spinner selectedMonthSpinner;
    int selectedMonthNumber;
    String selectedMonthName;
    View view;
    TextView emptyListView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private long message_id;
    private ProgressDialog progressDialog;


    public CalendarEventFragment() {
        //Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarEventFragment newInstance(String param1, String param2) {
        CalendarEventFragment fragment = new CalendarEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        message_id = intent.getIntExtra("ID", 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.content_calendar_events, container, false);

        calendarView = (RecyclerView) view.findViewById(R.id.schoolCalendarView);
        calendarView.setHasFixedSize(true);

        LinearLayoutManager calendarLayoutManager = new LinearLayoutManager(getContext());
        calendarLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(getContext());

        db = handbookDbHelper.getReadableDatabase();

        calendarView.setLayoutManager(calendarLayoutManager);


        /*Code to get spiiner values of calendar
        * Note that the month numbers are 0-based, so at the time of this writing (in April) the month number will be 3.
        * */

        Calendar c = Calendar.getInstance();
        //selectedMonth = c.get(Calendar.MONTH)+1;

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        selectedMonthName = month_date.format(c.getTime());
        setupMonthSpinner(selectedMonthName);
        selectedMonthSpinner.setOnItemSelectedListener(this);

        if (!sharedPreferences.getBoolean(QuickstartPreferences.SCHOOL_CALENDER_EVENTS_DOWNLOADED, false)) {

            SchoolProfile schoolProfile = HandBookDbHelper.loadSchoolProfileFromDB(db);
            if (schoolProfile != null && schoolProfile.getSchoolId() != null) {

                progressDialog = ProgressDialog.show(getContext(), "Downloading calendar", "Please wait", false);
                FetchSchoolCalendarAsyncTask.CalendarDownloadedListener getEvents = new FetchSchoolCalendarAsyncTask.CalendarDownloadedListener() {
                    @Override
                    public void onFinished(List<Event> currentEvents) {
                        events = currentEvents;
                    }
                };

                FetchSchoolCalendarAsyncTask.CalendarDownloadedListener saveEventsToDB =
                        new FetchSchoolCalendarAsyncTask.CalendarDownloadedListener() {
                            @Override
                            public void onFinished(List<Event> currentEvents) {
                                CalendarEvents.saveSchoolCalendarEventsToDB(db, currentEvents, sharedPreferences);
                                Log.v("CalenderEventsDBAct", "Saved to DB");
                            }
                        };


                FetchSchoolCalendarAsyncTask.CalendarDownloadedListener setupView = new FetchSchoolCalendarAsyncTask.CalendarDownloadedListener() {
                    @Override
                    public void onFinished(List<Event> currentEvents) {
                        setupSchoolCalendarView(currentEvents);
                    }
                };

                FetchSchoolCalendarAsyncTask.CalendarDownloadedListener clearBusyDialog = new FetchSchoolCalendarAsyncTask.CalendarDownloadedListener() {
                    @Override
                    public void onFinished(List<Event> events) {
                        progressDialog.dismiss();
                    }
                };

                List<FetchSchoolCalendarAsyncTask.CalendarDownloadedListener> listeners = new ArrayList<>();
                listeners.add(getEvents);
                listeners.add(saveEventsToDB);
                listeners.add(setupView);

                listeners.add(clearBusyDialog);

                FetchSchoolCalendarAsyncTask task = new FetchSchoolCalendarAsyncTask(listeners, schoolProfile.getSchoolId());
                task.execute();
            } else {
                //School profile not yet fetched or fetched incorrectly
                Toast.makeText(getContext(), "Failed to fetch school calendar. Please click refresh menu item and restart app.", Toast.LENGTH_LONG);
            }
        } else {
            //Time table has been downloaded just fetch from DB render it
            loadEventsFromDB(db, selectedMonthNumber);
            /*List<Event> currentEvents = HandBookDbHelper.loadSchoolCalendarfromDb(db, selectedMonthNumber);
            Log.v("CalenderEventsDBAct", "loading from DB");
            events=currentEvents;
            setupSchoolCalendarView(events);*/
        }
        emptyListView = (TextView) view.findViewById(R.id.empty_list_view);
        ImageView img_prev_month = (ImageView) view.findViewById(R.id.img_month_previous);
        ImageView img_nxt_month = (ImageView) view.findViewById(R.id.img_month_next);
        img_nxt_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedMonthNumber >= 12) {
                    selectedMonthNumber = 1;
                } else {
                    selectedMonthNumber = selectedMonthNumber + 1;
                    if (selectedMonthNumber == 12) {
                        selectedMonthNumber = 11;
                    }
                }


                selectedMonthSpinner.setSelection(selectedMonthNumber);
                //-loadEventsFromDB(db,selectedMonthNumber);

            }
        });

        img_prev_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMonthNumber <= 1) {
                    selectedMonthNumber = 1;
                } else {
                    selectedMonthNumber = selectedMonthNumber - 1;
                }
                selectedMonthSpinner.setSelection(selectedMonthNumber);
                //loadEventsFromDB(db,selectedMonthNumber);
            }
        });


        return view;
    }

    /*Load events from database*/
    private void loadEventsFromDB(SQLiteDatabase db, int monthSelected) {
        List<Event> currentEvents = HandBookDbHelper.loadSchoolCalendarfromDb(db, monthSelected);
        Log.v("CalenderEventsDBAct", "loading from DB");

        events = currentEvents;
        setupSchoolCalendarView(events);
    }

    /*Setup spinner*/
    private void setupMonthSpinner(String selectedMonthName) {
        selectedMonthSpinner = (Spinner) view.findViewById(R.id.spin_calender_month);

        // Create an ArrayAdapter using the string array and a default spinner layout
        monthAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.cal_month_name, R.layout.simple_spinner_dropdown_item);

        // Specify the layout to use when the list of choices appears
        monthAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        selectedMonthSpinner.setAdapter(monthAdapter);

        if (!selectedMonthName.equals(null)) {

            /* Note that the month numbers are 0-based, so at the time of this writing (in April) the month number will be 3.
     * since we added +1 while fetching current default month hence we need to -1 while we match spinner value-postion
     * with selected month*/
            // int tempMonth=selectedMonth-1;
            selectedMonthNumber = monthAdapter.getPosition(selectedMonthName);

            selectedMonthSpinner.setSelection(selectedMonthNumber);
        }
    }

    private void setupSchoolCalendarView(List<Event> currentEvents) {

        if (currentEvents != null) {
            SchoolCalendarAdapter adapter = new SchoolCalendarAdapter(getContext(), currentEvents, HttpConnectionUtil.ViewType.DETAIL,
                    this);
            calendarView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
       /* if (currentEvents.size()==0){emptyListView.setVisibility(View.VISIBLE);}else {emptyListView.setVisibility(View.INVISIBLE);}*/

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        int spinner_pos = selectedMonthSpinner.getSelectedItemPosition();
        String[] size_values = getResources().getStringArray(R.array.cal_month_values);
        String tempVal = size_values[spinner_pos];
        int calendarMonth = Integer.valueOf(size_values[spinner_pos]);

        /*  To format month recieved as single digit to double digit for sqlite db to identify month in double digit
        * */
        loadEventsFromDB(db, calendarMonth);
       /* List<Event> currentEvents = HandBookDbHelper.loadSchoolCalendarfromDb(db, selectedMonthNumber);
        Log.v("CalenderEventsDBAct", "loading from DB");
        events = currentEvents;
        setupSchoolCalendarView(events);*/

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    public void recyclerViewClicked(View v, int position) {
        switch (v.getId()) {
            case R.id.event_LikeButton:
                ImageButton likeButton = (ImageButton) v;
                v.setBackgroundResource(R.drawable.ic_favorite_fill);
                Toast.makeText(getContext(), "Like pressed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.event_AddToCalendarButton:
                //addToCalendar();
                Toast.makeText(getContext(), "Added to your calendar", Toast.LENGTH_SHORT).show();
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
        } catch (Exception ex) {
            Toast.makeText(getContext(), "Failed to add event to the calendar", Toast.LENGTH_SHORT);
        }
    }


}
