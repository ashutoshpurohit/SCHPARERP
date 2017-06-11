package com.myapp.handbook;

import android.content.Intent;
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

import com.myapp.handbook.Listeners.RecycleViewClickListener;
import com.myapp.handbook.Listeners.TimeTableDbUpdateListener;
import com.myapp.handbook.Tasks.FetchProfileAsyncTask;
import com.myapp.handbook.Tasks.FetchSchoolCalendarAsyncTask;
import com.myapp.handbook.Tasks.FetchTimeTableAsyncTask;
import com.myapp.handbook.Tasks.UpdateNavigationViewHeader;
import com.myapp.handbook.adapter.DiaryNoteSummaryAdapter;
import com.myapp.handbook.adapter.SchoolCalendarAdapter;
import com.myapp.handbook.adapter.TimeTableSummaryAdapter;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.BaseTimeTable;
import com.myapp.handbook.domain.DiaryNote;
import com.myapp.handbook.domain.Event;
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
    RecyclerView homeSummaryView2;
    RecyclerView homeSummaryView3;
    TimeTableSummaryAdapter timetableAdapter;
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
        homeSummaryView2 =(RecyclerView)view.findViewById(R.id.summaryDiaryNotetView1);
        homeSummaryView3 = (RecyclerView)view.findViewById(R.id.summaryRecyclerView3);

        homeSummaryView2.setHasFixedSize(true);
        timeTableListView.setHasFixedSize(true);
        homeSummaryView3.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager diaryNoteLayoutManager = new LinearLayoutManager(getContext());
        diaryNoteLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        LinearLayoutManager summary3LayoutManager = new LinearLayoutManager(getContext());
        summary3LayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        timeTableListView.setLayoutManager(layoutManager);
        homeSummaryView2.setLayoutManager(diaryNoteLayoutManager);
        homeSummaryView3.setLayoutManager(summary3LayoutManager);

        setHasOptionsMenu(false);

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

                    if(profiles!=null && profiles.size() >0 ) {
                        HttpConnectionUtil.setSelectedProfileId(profiles.get(0).getId());
                        HttpConnectionUtil.setProfiles(profiles);
                    }
                }
            });
            //Download the profile
            new FetchProfileAsyncTask(profileDownloadListeners).execute();
        }
        else
        {
            allProfiles=HandBookDbHelper.LoadProfilefromDb(db);
            HttpConnectionUtil.setProfiles(allProfiles);
        }

        selectedProfileId = HttpConnectionUtil.getSelectedProfileId();//allProfiles.get(0).getId();

        selectedProfile = RoleProfile.getProfile(HttpConnectionUtil.getProfiles(), selectedProfileId);

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
            if(sharedPreferences.getBoolean(QuickstartPreferences.CALENDAR_DOWNLOADED+ "_" + selectedProfile.getId(), false)==false){
                FetchSchoolCalendarAsyncTask.CalendarDownloadedListener setupUI= new FetchSchoolCalendarAsyncTask.CalendarDownloadedListener() {
                    @Override
                    public void onFinished(List<Event> currentEvents) {
                        setupEventsView(selectedProfileId, selectedProfile.getProfileRole(),currentEvents);
                    }
                };
                List<FetchSchoolCalendarAsyncTask.CalendarDownloadedListener> listeners = new ArrayList<>();
                listeners.add(setupUI);
                new FetchSchoolCalendarAsyncTask(listeners).execute();
            }

            updateNavigationViewBasedOnProfileRole(allProfiles,fragmentView);
            new UpdateNavigationViewHeader(allProfiles,navigationView,getContext()).onSelectionChanged(selectedProfileId);
            setUpTimeTableView(profileTimeTable,selectedProfile.getProfileRole() );
            setupDiaryNotesView(selectedProfile.getProfileRole());
        }

        return view;
    }

    private void setupEventsView(String selectedProfileId, RoleProfile.ProfileRole role, List<Event> currentEvents) {

        if(role.equals(RoleProfile.ProfileRole.TEACHER)){
            if(currentEvents!=null && currentEvents.size()>0){
                RecycleViewClickListener launchEventsPage = new RecycleViewClickListener() {
                    @Override
                    public void recyclerViewClicked(View v, int position) {
                        Intent intent = new Intent(getContext(), CalendarEventsActivity.class);
                        getContext().startActivity(intent);
                    }
                };

                SchoolCalendarAdapter adapter = new SchoolCalendarAdapter(getContext(),currentEvents, HttpConnectionUtil.ViewType.SUMMARY,launchEventsPage);
                homeSummaryView3.setAdapter(adapter);
            }
        }
    }


    private void updateNavigationViewBasedOnProfileRole(List<RoleProfile> allProfiles, View fragmentView) {
        NavigationView navigationView= (NavigationView) getActivity().findViewById(R.id.navigation_view);
        Menu menuNav=navigationView.getMenu();

        /*MenuItem parentNote = menuNav.findItem(R.id.feedback);
        MenuItem itemTeacherNote = menuNav.findItem(R.id.teacherNote);
        if(parentNote!=null && checkRoleNotPresent(allProfiles, RoleProfile.ProfileRole.STUDENT))
        {
            parentNote.setVisible(false);
        }
        if(itemTeacherNote!=null && checkRoleNotPresent(allProfiles, RoleProfile.ProfileRole.TEACHER))
        {
            itemTeacherNote.setVisible(false);
        }*/
    }

    private boolean checkRoleNotPresent(List<RoleProfile> allProfiles, RoleProfile.ProfileRole role) {
        for (RoleProfile profile:allProfiles
             ) {
            if(profile.getProfileRole().equals(role))
                return false;
        }
        return true;
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

    private void setupDiaryNotesView(RoleProfile.ProfileRole role) {
        List<DiaryNote> latestDiaryNotes = new ArrayList<>();
        List<DiaryNote> latestHomeWork = new ArrayList<>();

        if(role.equals(RoleProfile.ProfileRole.STUDENT)) {

            latestDiaryNotes = HandBookDbHelper.loadLatestDiaryNote(db, HttpConnectionUtil.DIARY_NOTE_TYPE, selectedProfileId, 5);
            latestHomeWork = HandBookDbHelper.loadLatestHomework(db, HttpConnectionUtil.HOMEWORK_TYPE, selectedProfileId, 5);

            homeWorkSummaryAdapter = new DiaryNoteSummaryAdapter(getActivity(), latestHomeWork);
            homeSummaryView3.setAdapter(homeWorkSummaryAdapter);
        }
        if(role.equals(RoleProfile.ProfileRole.TEACHER)){
            latestDiaryNotes =HandBookDbHelper.loadLatestDiaryNote(db,HttpConnectionUtil.PARENT_NOTE_TYPE,selectedProfileId,3);

        }
        diaryNoteSummaryAdapter = new DiaryNoteSummaryAdapter(getActivity(), latestDiaryNotes);
        homeSummaryView2.setAdapter(diaryNoteSummaryAdapter);
    }


    public void setUpTimeTableView(BaseTimeTable table, RoleProfile.ProfileRole role) {

        if(table!=null){

            int dayOfWeek = 1;//getDayOfTheWeek();
            List<WeeklyTimeTable> weekly= table.getWeeklyTimeTableList();
            List<TimeSlots> todaysTimeSlot=null;

            if(dayOfWeek > -1) {

                todaysTimeSlot = weekly.get(dayOfWeek).getTimeSlotsList();
                timetableAdapter = new TimeTableSummaryAdapter(getContext(), todaysTimeSlot,role);
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
