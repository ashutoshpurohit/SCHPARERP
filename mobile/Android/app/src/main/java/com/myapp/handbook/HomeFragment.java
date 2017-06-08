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
import android.widget.TextView;

import com.myapp.handbook.Listeners.TimeTableDbUpdateListener;
import com.myapp.handbook.Tasks.FetchProfileAsyncTask;
import com.myapp.handbook.Tasks.FetchTimeTableAsyncTask;
import com.myapp.handbook.adapter.DiaryNoteSummaryAdapter;
import com.myapp.handbook.adapter.TimeTableRecylerViewAdapter;
import com.myapp.handbook.adapter.TimeTableViewType;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.BaseTimeTable;
import com.myapp.handbook.domain.DiaryNote;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.SchoolProfile;
import com.myapp.handbook.domain.TimeSlots;
import com.myapp.handbook.domain.WeeklyTimeTable;

import java.util.ArrayList;
import java.util.List;

import static com.myapp.handbook.domain.RoleProfile.savetoDB;

public class HomeFragment extends Fragment {

    private static final String TAG = "ProfileEntry Fetch";
    private List<RoleProfile> allProfiles = new ArrayList<>();
    private SchoolProfile schoolProfile = null;



    private NavigationView navigationView=null;
    private View fragmentView;
    private SQLiteDatabase db;
    private Cursor cursor;
    View header;
    BaseTimeTable profileTimeTable;
    String selectedProfileId;
    SharedPreferences sharedPreferences;
    RecyclerView timeTableListView;
    RoleProfile selectedProfile;
    RecyclerView diaryNoteSummaryView;
    RecyclerView homeWorkSummaryView;
    TimeTableRecylerViewAdapter timetableAdapter;
    DiaryNoteSummaryAdapter diaryNoteSummaryAdapter;
    DiaryNoteSummaryAdapter homeWorkSummaryAdapter;

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
        homeWorkSummaryView = (RecyclerView)view.findViewById(R.id.summaryRecyclerView3);

        diaryNoteSummaryView.setHasFixedSize(true);
        timeTableListView.setHasFixedSize(true);
        homeWorkSummaryView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager diaryNoteLayoutManager = new LinearLayoutManager(getContext());
        diaryNoteLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        LinearLayoutManager summary3LayoutManager = new LinearLayoutManager(getContext());
        summary3LayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        timeTableListView.setLayoutManager(layoutManager);
        diaryNoteSummaryView.setLayoutManager(diaryNoteLayoutManager);
        homeWorkSummaryView.setLayoutManager(summary3LayoutManager);

        setHasOptionsMenu(true);

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());

        db = handbookDbHelper.getReadableDatabase();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (sharedPreferences.getBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, false) == false) {

            List<FetchProfileAsyncTask.ProfileDownloadListener> profileDownloadListeners = new ArrayList<>();
            profileDownloadListeners.add(new FetchProfileAsyncTask.ProfileDownloadListener() {
                @Override
                public void onProfileDownload(List<RoleProfile> profiles) {

                    savetoDB(profiles,db,sharedPreferences);
                }
            });

            profileDownloadListeners.add(new FetchProfileAsyncTask.ProfileDownloadListener() {
                @Override
                public void onProfileDownload(List<RoleProfile> profiles) {

                    if(profiles!=null && profiles.size() >0 )
                        HttpConnectionUtil.setSelectedProfileId(profiles.get(0).getId());
                    HttpConnectionUtil.setProfiles(profiles);
                }
            });
            //Download the profile
            new FetchProfileAsyncTask(profileDownloadListeners).execute();
        }
        else
        {
            allProfiles=HandBookDbHelper.LoadProfilefromDb(db);
        }

        selectedProfileId = HttpConnectionUtil.getSelectedProfileId();//allProfiles.get(0).getId();

        selectedProfile = RoleProfile.getProfile(db, selectedProfileId);



        if(selectedProfile!=null)
        {
            customizeScreenBasedOnProfile(selectedProfile.getProfileRole(), fragmentView);

            if (sharedPreferences.getBoolean(QuickstartPreferences.TIMETABLE_DOWNLOADED + "_" + selectedProfile.getId(), false) == false) {

                List<FetchTimeTableAsyncTask.TaskListener> listeners = new ArrayList<>();
                listeners.add(new TimeTableDbUpdateListener(db, selectedProfile, sharedPreferences));
                listeners.add(new FetchTimeTableAsyncTask.TaskListener() {
                    @Override
                    public void onFinished(BaseTimeTable table) {
                        setUpTimeTableView(table,selectedProfile.getProfileRole() );
                    }
                });
                new FetchTimeTableAsyncTask(selectedProfile, listeners).execute();
            }
            else {

                profileTimeTable = HandBookDbHelper.loadTimeTable(db, selectedProfileId, selectedProfile.getProfileRole());
            }
            setUpTimeTableView(profileTimeTable,selectedProfile.getProfileRole() );
        }
        setupDiaryNotesView();
        return view;
    }

    private void customizeScreenBasedOnProfile(RoleProfile.ProfileRole role, View view) {

        TextView summaryView2Header = (TextView) view.findViewById(R.id.summaryView2Header);
        TextView summaryView3Header = (TextView) view.findViewById(R.id.summaryView3Header);
        if(role.equals(RoleProfile.ProfileRole.TEACHER)){

            summaryView2Header.setText(getResources().getString(R.string.parent_messages));
            summaryView3Header.setText(getResources().getString(R.string.school_events));
        }
        else if(role.equals(RoleProfile.ProfileRole.STUDENT)){

            summaryView2Header.setText(getResources().getString(R.string.diary_notes));
            summaryView3Header.setText(getResources().getString(R.string.Homework));

        }
    }

    private void setupDiaryNotesView() {

        List<DiaryNote> latestDiaryNotes = new ArrayList<>();
        List<DiaryNote> latestHomeWork = new ArrayList<>();
        latestDiaryNotes = HandBookDbHelper.loadLatestDiaryNote(db,HttpConnectionUtil.DIARY_NOTE_TYPE,selectedProfileId,3);
        latestHomeWork = HandBookDbHelper.loadLatestDiaryNote(db,HttpConnectionUtil.HOMEWORK_TYPE,selectedProfileId,3);
        diaryNoteSummaryAdapter = new DiaryNoteSummaryAdapter(getContext(), latestDiaryNotes);
        diaryNoteSummaryView.setAdapter(diaryNoteSummaryAdapter);
        homeWorkSummaryAdapter = new DiaryNoteSummaryAdapter(getContext(),latestHomeWork);
        homeWorkSummaryView.setAdapter(homeWorkSummaryAdapter);

    }


    public void setUpTimeTableView(BaseTimeTable table, RoleProfile.ProfileRole role) {

        if(table!=null){

            int dayOfWeek = 1;//getDayOfTheWeek();
            List<WeeklyTimeTable> weekly= table.getWeeklyTimeTableList();
            List<TimeSlots> todaysTimeSlot=null;

            if(dayOfWeek > -1) {

                todaysTimeSlot = weekly.get(dayOfWeek).getTimeSlotsList();
                timetableAdapter = new TimeTableRecylerViewAdapter(getContext(), todaysTimeSlot,role);
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