package com.myapp.handbook;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.myapp.handbook.Listeners.SelectionChangeListener;
import com.myapp.handbook.Tasks.FetchProfileAsyncTask;
import com.myapp.handbook.Tasks.UpdateNavigationViewHeader;
import com.myapp.handbook.adapter.ProfileAdapter;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.SchoolProfile;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.myapp.handbook.domain.RoleProfile.savetoDB;

public class TopFragment extends Fragment {

    private static final String TAG = "ProfileEntry Fetch";
    private List<RoleProfile> allProfiles = new ArrayList<>();
    private SchoolProfile schoolProfile = null;



    private NavigationView navigationView=null;
    private View fragmentView;
    private SQLiteDatabase db;
    private Cursor cursor;
    View header;
    SharedPreferences sharedPreferences;
    ListView listView;

    public void setNavigationView(NavigationView navigationView) {
        this.navigationView = navigationView;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        fragmentView = view;

        //header = inflater.inflate(R.layout.listview_profile_header, null);
        listView= (ListView) view.findViewById(R.id.profileListView1);
        //listView.addHeaderView(header);

        setHasOptionsMenu(true);

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());

        db = handbookDbHelper.getReadableDatabase();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        List<FetchProfileAsyncTask.ProfileDownloadListener> listeners = new ArrayList<>();
        listeners.add(new FetchProfileAsyncTask.ProfileDownloadListener() {
            @Override
            public void onProfileDownload(List<RoleProfile> profiles) {

                savetoDB(profiles,db,sharedPreferences);
            }
        });

        listeners.add(new FetchProfileAsyncTask.ProfileDownloadListener(){

            @Override
            public void onProfileDownload(List<RoleProfile> profiles) {

                SetUpView(profiles,fragmentView);
            }
        });
        //sharedPreferences.edit().putBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, false).apply();
        if (sharedPreferences.getBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, false) == false) {
            //Download the profile
            new FetchProfileAsyncTask(listeners).execute();

        } else
        {
            allProfiles=HandBookDbHelper.LoadProfilefromDb(db);
        }


        SetUpView(allProfiles,fragmentView);
        //UpdateSchoolDetails(schoolProfile);
        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem item=menu.findItem(R.id.action_search);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    public void SetUpView(List<RoleProfile> allProfiles, View fragmentView) {

        View view = fragmentView;

        //TextView headerText = (TextView) header.findViewById(R.id.profileHeader);
        if (!allProfiles.isEmpty()) {
            RoleProfile [] profiles =new RoleProfile[allProfiles.size()];
            profiles=   allProfiles.toArray(profiles);
            List<SelectionChangeListener> selectionChangedListeners = new ArrayList<>();
            selectionChangedListeners.add(new UpdateNavigationViewHeader(allProfiles,navigationView,getContext()));
            ProfileAdapter adapter = new ProfileAdapter(getContext(),R.layout.list_item_profile,profiles,selectionChangedListeners);

            listView.setAdapter(adapter);
            //headerText.setText("Profile");
        } else {
            //firstName.setText("Loading the profile info. Please wait..");

            //headerText.setText("Loading ..");
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
