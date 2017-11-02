package com.myapp.handbook;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.myapp.handbook.Tasks.FetchHolidayListsAsyncTask;
import com.myapp.handbook.adapter.HolidayListsAdapter;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.HolidayLists;
import com.myapp.handbook.domain.SchoolHolidayLists;
import com.myapp.handbook.domain.SchoolProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashutosh on 10/16/2017.
 */

public class CalendarHolidayEventFragment extends Fragment {
    View view;
    /*ListView holidayListView;*/
    List<HolidayLists> holidayLists;
    RecyclerView holidayView;
    private SharedPreferences sharedPreferences;
    private SQLiteDatabase db;
    private ProgressDialog progressDialog;

    public CalendarHolidayEventFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.content_holiday_lists, container, false);

        holidayView = (RecyclerView) view.findViewById(R.id.holidayRecView);
        holidayView.setHasFixedSize(true);

        LinearLayoutManager holidayLayoutManager = new LinearLayoutManager(getContext());
        holidayLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(getContext());

        db = handbookDbHelper.getReadableDatabase();
        holidayView.setLayoutManager(holidayLayoutManager);


        //holidayListView.setEmptyView(view.findViewById(R.id.empty_holiday_list_view));

        if (!sharedPreferences.getBoolean(QuickstartPreferences.SCHOOL_HOLIDAY_LISTS_DOWNLOADED, false)) {
            SchoolProfile schoolProfile = HandBookDbHelper.loadSchoolProfileFromDB(db);
            if (schoolProfile != null && schoolProfile.getSchoolId() != null) {

                progressDialog = ProgressDialog.show(getContext(), "Holiday Lists Downloading", "Please wait", false);


                FetchHolidayListsAsyncTask.HolidayListsDownloadedListener getHolidayLists =
                        new FetchHolidayListsAsyncTask.HolidayListsDownloadedListener() {
                            @Override
                            public void onFinished(List<HolidayLists> currentHolidayLists) {
                                holidayLists = currentHolidayLists;
                            }
                        };

                FetchHolidayListsAsyncTask.HolidayListsDownloadedListener saveHolidayListsToDB =
                        new FetchHolidayListsAsyncTask.HolidayListsDownloadedListener() {
                            @Override
                            public void onFinished(List<HolidayLists> currentHolidayLists) {
                                SchoolHolidayLists.saveHolidayListsToDB(db, currentHolidayLists, sharedPreferences);
                                Log.v("HolidayListsDBAct", "Saved to DB");
                            }
                        };


                FetchHolidayListsAsyncTask.HolidayListsDownloadedListener setupView =
                        new FetchHolidayListsAsyncTask.HolidayListsDownloadedListener() {
                            @Override
                            public void onFinished(List<HolidayLists> currentHolidayLists) {
                                setupHolidayView(currentHolidayLists);
                            }
                        };

                FetchHolidayListsAsyncTask.HolidayListsDownloadedListener clearBusyDialog =
                        new FetchHolidayListsAsyncTask.HolidayListsDownloadedListener() {
                            @Override
                            public void onFinished(List<HolidayLists> holidayLists) {
                                progressDialog.dismiss();
                            }
                        };

                List<FetchHolidayListsAsyncTask.HolidayListsDownloadedListener> listeners = new ArrayList<>();
                listeners.add(getHolidayLists);
                listeners.add(saveHolidayListsToDB);
                listeners.add(setupView);

                listeners.add(clearBusyDialog);

                FetchHolidayListsAsyncTask task = new FetchHolidayListsAsyncTask(listeners, schoolProfile.getSchoolId());
                task.execute();
            } else {
                //School profile not yet fetched or fetched incorrectly
                Toast.makeText(getContext(), "Failed to fetch school holiday lists. Please click refresh menu item and restart app.", Toast.LENGTH_LONG);
            }
        } else {
            //Holiday has been downloaded just fetch from DB render it
            loadHolidayListsfromDb(db);
        }
        return view;
    }

    /*Load events from database*/
    private void loadHolidayListsfromDb(SQLiteDatabase db) {
        List<HolidayLists> currentHolidayLists = HandBookDbHelper.loadHolidayListsfromDb(db);

        holidayLists = currentHolidayLists;
        setupHolidayView(holidayLists);
    }

    private void setupHolidayView(List<HolidayLists> currentHolidayLists) {

        if (currentHolidayLists != null) {
            HolidayListsAdapter adapter = new HolidayListsAdapter(getContext(), currentHolidayLists);
            holidayView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }


    /*public void recyclerViewClicked(View v, int position) {
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
*/

}
