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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.myapp.handbook.domain.CalendarEvents;
import com.myapp.handbook.domain.DiaryNote;
import com.myapp.handbook.domain.Event;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.SchoolProfile;
import com.myapp.handbook.domain.TimeSlots;
import com.myapp.handbook.domain.TimeTable;
import com.myapp.handbook.domain.WeeklyTimeTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.myapp.handbook.domain.RoleProfile.AddWelcomeMessage;
import static com.myapp.handbook.domain.RoleProfile.saveProfilestoDB;
import static com.myapp.handbook.domain.RoleProfile.saveSchoolProfiletoDB;

public class HomeFragment extends Fragment {

    private static final String TAG = "ProfileEntry Fetch";
    View header;
    BaseTimeTable profileTimeTable;
    String selectedProfileId;
    SharedPreferences sharedPreferences;
    RecyclerView timeTableListView;
    TextView emptyTimeTableView;
    RoleProfile selectedProfile;
    RecyclerView homeSummaryView2;
    RecyclerView homeSummaryView3;
    TimeTableSummaryAdapter timetableAdapter;
    DiaryNoteSummaryAdapter diaryNoteSummaryAdapter;
    DiaryNoteSummaryAdapter homeWorkSummaryAdapter;
    private List<RoleProfile> allProfiles = new ArrayList<>();
    private SchoolProfile schoolProfile = null;
    private NavigationView navigationView = null;

    private View fragmentView;
    private SQLiteDatabase db;
    private Cursor cursor;

    public void setNavigationView(NavigationView navigationView) {
        this.navigationView = navigationView;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fragmentView = view;

        timeTableListView = (RecyclerView) view.findViewById(R.id.summaryTimetableListView1);
        emptyTimeTableView =(TextView)view.findViewById(R.id.empty_summary_timetable_view);
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

        if (!sharedPreferences.getBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, false)) {

            List<FetchProfileAsyncTask.ProfileDownloadListener> profileDownloadListeners = new ArrayList<>();
            profileDownloadListeners.add(new FetchProfileAsyncTask.ProfileDownloadListener() {
                @Override
                public void onProfileDownload(List<RoleProfile> profiles, SchoolProfile schoolProfile) {

                    saveProfilestoDB(profiles,db,sharedPreferences);
                }
            });

            profileDownloadListeners.add(new FetchProfileAsyncTask.ProfileDownloadListener() {
                @Override
                public void onProfileDownload(List<RoleProfile> profiles, SchoolProfile schoolProfile) {

                    saveSchoolProfiletoDB(schoolProfile,db,sharedPreferences);
                }
            });


            profileDownloadListeners.add(new FetchProfileAsyncTask.ProfileDownloadListener() {
                @Override
                public void onProfileDownload(List<RoleProfile> profiles, SchoolProfile schoolProfile) {

                    if (!sharedPreferences.getBoolean(QuickstartPreferences.WELCOME_MESSAGE_ADDED, false)) {
                        AddWelcomeMessage(profiles, db);
                        sharedPreferences.edit().putBoolean(QuickstartPreferences.WELCOME_MESSAGE_ADDED, true).commit();
                    }
                }
            });

            profileDownloadListeners.add(new FetchProfileAsyncTask.ProfileDownloadListener() {
                @Override
                public void onProfileDownload(List<RoleProfile> profiles, SchoolProfile schoolProfile) {

                    if(profiles!=null && profiles.size() >0 ) {
                        HttpConnectionUtil.setSelectedProfileId(profiles.get(0).getId());
                        HttpConnectionUtil.setProfiles(profiles);
                    }
                }
            });
            profileDownloadListeners.add(new FetchProfileAsyncTask.ProfileDownloadListener() {
                @Override
                public void onProfileDownload(List<RoleProfile> profiles, SchoolProfile schoolProfile) {

                    updateOtherViewBasedOnSelectedProfile();
                }
            });
            new FetchProfileAsyncTask(profileDownloadListeners, getContext()).execute();
        }
        else
        {
            allProfiles=HandBookDbHelper.LoadProfilefromDb(db);
            HttpConnectionUtil.setProfiles(allProfiles);
            updateOtherViewBasedOnSelectedProfile();
        }
        return view;
    }

    private void updateOtherViewBasedOnSelectedProfile() {
        selectedProfileId = HttpConnectionUtil.getSelectedProfileId();//allProfiles.get(0).getId();

        selectedProfile = RoleProfile.getProfile(HttpConnectionUtil.getProfiles(), selectedProfileId);

        if(selectedProfile!=null)
        {
            customizeScreenBasedOnProfile(selectedProfile.getProfileRole(), fragmentView);

            if (!sharedPreferences.getBoolean(QuickstartPreferences.TIMETABLE_DOWNLOADED + "_" + selectedProfile.getId(), false)) {

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
            if (!sharedPreferences.getBoolean(QuickstartPreferences.SCHOOL_CALENDER_EVENTS_DOWNLOADED, false)) {

                FetchSchoolCalendarAsyncTask.CalendarDownloadedListener setupUI= new FetchSchoolCalendarAsyncTask.CalendarDownloadedListener() {
                    @Override
                    public void onFinished(List<Event> currentEvents) {
                        setupEventsView(selectedProfileId, selectedProfile.getProfileRole(),currentEvents);
                    }
                };

                FetchSchoolCalendarAsyncTask.CalendarDownloadedListener saveEventsToDB =
                        new FetchSchoolCalendarAsyncTask.CalendarDownloadedListener() {
                            @Override
                            public void onFinished(List<Event> currentEvents) {
                                CalendarEvents.saveSchoolCalendarEventsToDB(db,currentEvents,sharedPreferences);
                                Log.v("CalenderEventsDBAct", "Saved to DB");
                            }
                        };

                List<FetchSchoolCalendarAsyncTask.CalendarDownloadedListener> listeners = new ArrayList<>();
                listeners.add(setupUI);
                listeners.add(saveEventsToDB);
                String schoolId =HttpConnectionUtil.getSchoolId();
                if(schoolId!=null)
                    new FetchSchoolCalendarAsyncTask(listeners,schoolId).execute();
            }
            else{
                /*Added selected month code for calender month selection on School Calendar page*/
                Calendar c = Calendar.getInstance();
                int selectedMonth = c.get(Calendar.MONTH) + 1;
                List<Event> currentEvents = HandBookDbHelper.loadSchoolCalendarfromDb(db, selectedMonth);
                setupEventsView(selectedProfileId,selectedProfile.getProfileRole(),currentEvents);
            }

            updateNavigationViewBasedOnProfileRole(allProfiles,fragmentView);
            new UpdateNavigationViewHeader(allProfiles,navigationView,getContext()).onSelectionChanged(selectedProfileId);
            setUpTimeTableView(profileTimeTable,selectedProfile.getProfileRole() );
            setupDiaryNotesView(selectedProfile.getProfileRole());
        }
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

            latestDiaryNotes = HandBookDbHelper.loadLatestDiaryNote(db, HttpConnectionUtil.DIARY_NOTE_TYPE, selectedProfileId, 3);
            latestHomeWork = HandBookDbHelper.loadLatestHomework(db, HttpConnectionUtil.HOMEWORK_TYPE, selectedProfileId, 3);

            homeWorkSummaryAdapter = new DiaryNoteSummaryAdapter(getActivity(), latestHomeWork, HttpConnectionUtil.HOMEWORK_TYPE);
            homeSummaryView3.setAdapter(homeWorkSummaryAdapter);
        }
        if(role.equals(RoleProfile.ProfileRole.TEACHER)){
            latestDiaryNotes =HandBookDbHelper.loadLatestDiaryNote(db,HttpConnectionUtil.PARENT_NOTE_TYPE,selectedProfileId,3);

        }
        diaryNoteSummaryAdapter = new DiaryNoteSummaryAdapter(getActivity(), latestDiaryNotes, HttpConnectionUtil.DIARY_NOTE_TYPE);
        homeSummaryView2.setAdapter(diaryNoteSummaryAdapter);
    }


    public void setUpTimeTableView(BaseTimeTable table, RoleProfile.ProfileRole role) {

        if(table!=null){

            //int dayOfWeek = 1;//getDayOfTheWeek();
            Calendar calendar = Calendar.getInstance();
            //int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date().getTime());
            List<WeeklyTimeTable> weekly= table.getWeeklyTimeTableList();
            List<TimeSlots> todaysTimeSlot= TimeTable.getTimeSlot(table,dayOfWeek);
            //if(dayOfWeek > -1)
            if(todaysTimeSlot!=null && todaysTimeSlot.size()>0)
            {
                List<TimeSlots> sortedTimeSlots = sortTimeSlots(todaysTimeSlot);//todaysTimeSlot;// ;
                timetableAdapter = new TimeTableSummaryAdapter(getContext(), sortedTimeSlots,role);
                timeTableListView.setAdapter(timetableAdapter);
                timetableAdapter.notifyDataSetChanged();
                emptyTimeTableView.setVisibility(View.INVISIBLE);

            }
            else{
                emptyTimeTableView.setVisibility(View.VISIBLE);
                timeTableListView.setVisibility(View.INVISIBLE);
            }

        }

    }

    List<TimeSlots> sortTimeSlots(List<TimeSlots> timeSlotsToSort){
        Collections.sort(timeSlotsToSort, new Comparator<TimeSlots>() {
            @Override
            public int compare(TimeSlots t1, TimeSlots t2) {
                try {
                    String t1StartTime = t1.getStartTime();
                    String t2StartTime = t2.getStartTime();
                    int t1StartTimeHour=Integer.parseInt(t1StartTime.split(":")[0]);
                    int t1StartTimeMin=Integer.parseInt(t1StartTime.split(":")[1]);

                    int t2StartTimeHour=Integer.parseInt(t2StartTime.split(":")[0]);
                    int t2StartTimeMin=Integer.parseInt(t2StartTime.split(":")[1]);

                    if(t1StartTimeHour < t2StartTimeHour)
                        return  -1;
                    else if(t1StartTimeHour > t2StartTimeHour)
                        return 1;
                    else if(t1StartTimeHour==t2StartTimeHour){
                        if(t1StartTimeMin < t2StartTimeMin)
                            return -1;
                        else if(t1StartTimeMin > t2StartTimeMin)
                            return 1;
                        else
                            return 0;
                    }
                    else
                        return 0;


                }
                catch (Exception e){
                    return 0;
                }
            }
        });
        return timeSlotsToSort;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        //Hide search menu icon
        menu.getItem(0).setVisible(false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("SchoolLink");
    }

    @Override
    public void onResume(){

        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("SchoolLink");
    }


}
