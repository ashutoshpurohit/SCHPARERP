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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

        header = inflater.inflate(R.layout.listview_profile_header, null);
        listView= (ListView) view.findViewById(R.id.profileListView1);
        listView.addHeaderView(header);
        setHasOptionsMenu(true);

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());

        db = handbookDbHelper.getReadableDatabase();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        //sharedPreferences.edit().putBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, false).apply();
        if (sharedPreferences.getBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, false) == false) {
            //Download the profile
            new FetchProfileAsyncTask().execute();

        } else
        {
            allProfiles=HandBookDbHelper.LoadProfilefromDb(db);
        }

        SetUpView();
        UpdateSchoolDetails(schoolProfile);
        return view;
    }



    public void SetUpView() {

        View view = fragmentView;
        /*TextView firstName = (TextView) view.findViewById(R.id.firstName);
        ImageView profileImage = (ImageView)view.findViewById(R.id.profileImage);*/
        //profileImage.setImageDrawable();
        TextView headerText = (TextView) header.findViewById(R.id.profileHeader);
        if (!allProfiles.isEmpty()) {
            RoleProfile [] profiles =new RoleProfile[allProfiles.size()];
            profiles=   allProfiles.toArray(profiles);
            ProfileAdapter adapter = new ProfileAdapter(getContext(),R.layout.list_item_profile,profiles);
            listView.setAdapter(adapter);
            headerText.setText("Profile");
            /*RoleProfile profile = allProfiles.get(0);
            Picasso.with(getContext())
                    .load(profile.getImageUrl())
                    .placeholder(R.drawable.contact_picture_placeholder)
                    .error(R.drawable.contact_picture_error)
                    //.networkPolicy(NetworkPolicy.OFFLINE)
                    .into(profileImage);

            TextView middleName = (TextView) view.findViewById(R.id.middleName);
            TextView lastName = (TextView) view.findViewById(R.id.lastName);
            TextView role = (TextView) view.findViewById(R.id.role);
            TextView gender = (TextView) view.findViewById(R.id.gender);
            TextView dob = (TextView) view.findViewById(R.id.dob);
            TextView std = (TextView) view.findViewById(R.id.std);
            TextView address = (TextView) view.findViewById(R.id.address);

            firstName.setText("First Name :" + profile.getFirstName());
            middleName.setText("Middle Name :" + profile.getMiddleName());
            lastName.setText("Last Name :" + profile.getLastName());
            role.setText("Role :" + profile.getRole());
            gender.setText("Gender :" + profile.getGender());
            dob.setText("Date of Birth :" + profile.getBirth_date());
            std.setText("Std :" + profile.getStd());
            address.setText("Address :" + profile.getAddress());*/

        } else {
            //firstName.setText("Loading the profile info. Please wait..");

            headerText.setText("Loading ..");
        }

    }

    private void UpdateSchoolDetails(SchoolProfile profile) {

        View view = fragmentView;
        NavigationView navView =  navigationView;
        if(schoolProfile!=null){
            View header= navView.getHeaderView(0);
            TextView schoolName = (TextView) header.findViewById(R.id.schoolName);
            ImageView profileImage = (ImageView)header.findViewById(R.id.school_logo);

            Picasso.with(getContext())
                    .load(profile.getSchoolLogoImageURL())
                    .placeholder(R.drawable.contact_picture_placeholder)
                    .error(R.drawable.contact_picture_error)
                    .into(profileImage);
            schoolName.setText(profile.getSchoolName());

        }

    }

    class FetchProfileAsyncTask extends AsyncTask<Void, Void, List<RoleProfile>> {
        @Override
        protected List<RoleProfile> doInBackground(Void... params) {
            HttpConnectionUtil util = new HttpConnectionUtil();
            List<RoleProfile> profiles=null;
            String mobileNumber =HttpConnectionUtil.getMobileNumber();
            String url =HttpConnectionUtil.URL_ENPOINT + "/GetTeacherOrParentRole/" + mobileNumber;
            String result = util.downloadUrl(url, HttpConnectionUtil.RESTMethod.GET, null);
            Log.i(TAG, "Received JSON :" + result);
            try {
                profiles = new ArrayList<>();
                JSONObject jsonBody = new JSONObject(result);
                JSONArray students = null;
                JSONObject teacher = null;
                JSONObject school =null;

                if (jsonBody.has("School")) {
                    school = jsonBody.getJSONObject("School");

                    if(school!=null)
                        schoolProfile = SchoolProfile.parseJSonObject(school);
                }

                if (jsonBody.has("Students"))
                    students = jsonBody.getJSONArray("Students");
                if(jsonBody.has("Teacher") && !jsonBody.isNull("Teacher"))
                    teacher=jsonBody.getJSONObject("Teacher");
                for (int i = 0; i < students.length(); i++) {
                    RoleProfile profile = RoleProfile.parseStudentJSonObject(students.getJSONObject(i));
                    if (profile != null)
                        profiles.add(profile);
                }
                if (teacher != null) {
                    RoleProfile teacherProfile = RoleProfile.parseTeacherJSonObject(teacher);
                    if (teacherProfile != null)
                        profiles.add(teacherProfile);
                }
                return profiles;
            } catch (JSONException e) {
                Log.i(TAG, "Failed to parse JSON :" + result);
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(List<RoleProfile> profiles) {
            if(profiles!=null && profiles.size()>0) {
                allProfiles = profiles;
                sharedPreferences.edit().putBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, true).apply();
                // Toast.makeText(getActivity().getApplicationContext(), "Successfully downloaded the profiles from server)",
                //       Toast.LENGTH_LONG).show();
                SetUpView();
                UpdateSchoolDetails(schoolProfile);
                SavetoDB();

            }
        }
    }

    private void SavetoDB() {

        for(RoleProfile profile:allProfiles) {

            HandBookDbHelper.insertProfile(db,profile.getId(),profile.getFirstName(),profile.getLastName(),profile.getMiddleName(),profile.getRole(),profile.getGender(),profile.getStd(),profile.getAddress(),profile.getBirth_date(),profile.getImageUrl());
        }

        sharedPreferences.edit().putBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, true).commit();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        //Hide search menu icon
        menu.getItem(0).setVisible(false);
    }
}
