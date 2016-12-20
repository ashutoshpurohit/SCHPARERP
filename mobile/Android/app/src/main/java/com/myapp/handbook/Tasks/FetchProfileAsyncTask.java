package com.myapp.handbook.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.myapp.handbook.HttpConnectionUtil;
import com.myapp.handbook.QuickstartPreferences;
import com.myapp.handbook.domain.BaseTimeTable;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.SchoolProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAshutosh on 12/18/2016.
 */

public class FetchProfileAsyncTask extends AsyncTask<Void, Void, List<RoleProfile>> {

    public interface ProfileDownloadListener {
        public void onProfileDownload(List<RoleProfile> profiles);
    }

    SchoolProfile schoolProfile;
    List<RoleProfile> allProfiles;
    final List<ProfileDownloadListener> profileDownloadListeners;

    public FetchProfileAsyncTask(List<ProfileDownloadListener> listeners){

        this.profileDownloadListeners = listeners;
    }

    String TAG ="FetchProfileAsyncTask";
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
    /*        sharedPreferences.edit().putBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, true).commit();
            // Toast.makeText(getActivity().getApplicationContext(), "Successfully downloaded the profiles from server)",
            //       Toast.LENGTH_LONG).show();
            SetUpView();
            UpdateSchoolDetails(schoolProfile);
            SavetoDB();*/
            for (ProfileDownloadListener listener:profileDownloadListeners
                 ) {
                listener.onProfileDownload(allProfiles);
            }

        }
    }
}
