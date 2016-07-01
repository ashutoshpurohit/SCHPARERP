package com.myapp.handbook;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.data.HandbookContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TopFragment extends Fragment {

    private static final String TAG = "ProfileEntry Fetch";
    private List<Profile> allProfiles = new ArrayList<>();
    private View fragmentView;
    private SQLiteDatabase db;
    private Cursor cursor;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_top, container, false);
        fragmentView = view;


        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());

        db = handbookDbHelper.getReadableDatabase();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //sharedPreferences.edit().putBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, false).commit();

        if (sharedPreferences.getBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, false) == false) {
            //Download the profile
            new FetchProfileAsyncTask().execute();

        } else
        {
            allProfiles=HandBookDbHelper.LoadProfilefromDb(db);
        }

        SetUpView();

        return view;


    }



    public void SetUpView() {

        View view = fragmentView;
        TextView firstName = (TextView) view.findViewById(R.id.firstName);
        if (!allProfiles.isEmpty()) {
            Profile profile = allProfiles.get(0);

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
            address.setText("Address :" + profile.getAddress());
        } else {
            firstName.setText("Loading the profile info. Please wait..");
        }

    }

    private class FetchProfileAsyncTask extends AsyncTask<Void, Void, List<Profile>> {
        @Override
        protected List<Profile> doInBackground(Void... params) {
            HttpConnectionUtil util = new HttpConnectionUtil();
            List<Profile> profiles=null;
            String mobileNumber =HttpConnectionUtil.getMobileNumber();
            String url =HttpConnectionUtil.URL_ENPOINT + "/GetTeacherOrParentRole/" + mobileNumber;
            String result = util.downloadUrl(url, HttpConnectionUtil.RESTMethod.GET, null);
            Log.i(TAG, "Received JSON :" + result);
            try {
                profiles = new ArrayList<>();
                JSONObject jsonBody = new JSONObject(result);
                JSONArray students = null;
                JSONObject teacher = null;
                if (jsonBody.has("Students"))
                    students = jsonBody.getJSONArray("Students");
                /*if(jsonBody.has("Teacher"))
                    teacher=jsonBody.getJSONObject("Teacher");*/
                for (int i = 0; i < students.length(); i++) {
                    Profile profile = Profile.parseStudentJSonObject(students.getJSONObject(i));
                    if (profile != null)
                        profiles.add(profile);
                }
                if (teacher != null) {
                    Profile teacherProfile = Profile.parseStudentJSonObject(teacher);
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
        protected void onPostExecute(List<Profile> profiles) {
            allProfiles = profiles;
            sharedPreferences.edit().putBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, true).apply();
           // Toast.makeText(getActivity().getApplicationContext(), "Successfully downloaded the profiles from server)",
             //       Toast.LENGTH_LONG).show();
            SetUpView();
            SavetoDB();
        }
    }

    private void SavetoDB() {

        for(Profile profile:allProfiles) {

            HandBookDbHelper.insertProfile(db,profile.getId(),profile.getFirstName(),profile.getLastName(),profile.getMiddleName(),profile.getRole(),profile.getGender(),profile.getStd(),profile.getAddress(),profile.getBirth_date());
        }

        sharedPreferences.edit().putBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, true).commit();

    }
}
