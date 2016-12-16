package com.myapp.handbook;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myapp.handbook.adapter.DiaryNoteSummaryAdapter;
import com.myapp.handbook.adapter.TimeTableRecylerViewAdapter;
import com.myapp.handbook.adapter.TimeTableViewType;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.DiaryNote;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.SchoolProfile;
import com.myapp.handbook.domain.TimeSlots;
import com.myapp.handbook.domain.TimeTable;
import com.myapp.handbook.domain.WeeklyTimeTable;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "ProfileEntry Fetch";
    private List<RoleProfile> allProfiles = new ArrayList<>();
    private SchoolProfile schoolProfile = null;



    private NavigationView navigationView=null;
    private View fragmentView;
    private SQLiteDatabase db;
    private Cursor cursor;
    View header;
    TimeTable profileTimeTable;
    String selectedProfileId;
    SharedPreferences sharedPreferences;
    RecyclerView timeTableListView;
    RecyclerView diaryNoteSummaryView;
    TimeTableRecylerViewAdapter timetableAdapter;
    DiaryNoteSummaryAdapter diaryNoteSummaryAdapter;

    public void setNavigationView(NavigationView navigationView) {
        this.navigationView = navigationView;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fragmentView = view;

        timeTableListView = (RecyclerView) view.findViewById(R.id.summaryTimetableListView1);
        diaryNoteSummaryView =(RecyclerView)view.findViewById(R.id.summaryDiaryNotetView1);

        diaryNoteSummaryView.setHasFixedSize(true);
        timeTableListView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager diaryNoteLayoutManager = new LinearLayoutManager(getContext());
        diaryNoteLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        setHasOptionsMenu(true);

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());

        db = handbookDbHelper.getReadableDatabase();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        allProfiles=HandBookDbHelper.LoadProfilefromDb(db);

        selectedProfileId = HttpConnectionUtil.getSelectedProfileId();//allProfiles.get(0).getId();

        profileTimeTable = HandBookDbHelper.loadStudentTimeTable(db,selectedProfileId);

        timeTableListView.setLayoutManager(layoutManager);
        diaryNoteSummaryView.setLayoutManager(diaryNoteLayoutManager);


        setUpTimeTableView();
        setupDiaryNotesView();


        //UpdateSchoolDetails(schoolProfile);
        return view;
    }

    private void setupDiaryNotesView() {

        List<DiaryNote> latestDiaryNotes = new ArrayList<>();
        latestDiaryNotes = HandBookDbHelper.loadLatestDiaryNote(db,5);
        diaryNoteSummaryAdapter = new DiaryNoteSummaryAdapter(getContext(), latestDiaryNotes);
        diaryNoteSummaryView.setAdapter(diaryNoteSummaryAdapter);

    }


    public void setUpTimeTableView() {

        if(profileTimeTable!=null){

            int dayOfWeek = 1;//getDayOfTheWeek();
            List<WeeklyTimeTable> weekly= profileTimeTable.getWeeklyTimeTableList();
            List<TimeSlots> todaysTimeSlot=null;

            if(dayOfWeek > -1) {

                todaysTimeSlot = weekly.get(dayOfWeek).getTimeSlotsList();
                timetableAdapter = new TimeTableRecylerViewAdapter(getContext(), todaysTimeSlot);
                timetableAdapter.setType(TimeTableViewType.SUMMARY);
                timeTableListView.setAdapter(timetableAdapter);
                timetableAdapter.notifyDataSetChanged();

            }

        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        //Hide search menu icon
        menu.getItem(0).setVisible(false);
    }
}
