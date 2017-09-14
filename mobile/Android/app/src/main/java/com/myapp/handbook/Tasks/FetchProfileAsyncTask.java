package com.myapp.handbook.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.myapp.handbook.HttpConnectionUtil;
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
    final List<ProfileDownloadListener> profileDownloadListeners;
    SchoolProfile schoolProfile;
    List<RoleProfile> allProfiles;
    String TAG = "FetchProfileAsyncTask";
    Context context;
    ProgressDialog progressDialog;
    private DownloadCallback mCallback;

    public FetchProfileAsyncTask(List<ProfileDownloadListener> profileDownloadListeners, Context currentContext) {
        this.profileDownloadListeners=profileDownloadListeners;
        this.context=currentContext;

    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */

    @Override
    protected void onPreExecute() {
        if (mCallback != null) {
            NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update Callback with null data.
                mCallback.updateFromDownload(null);
                cancel(true);
            }
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Downloading information, Please wait..");
        progressDialog.show();
    }

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
            for (int i = 0; i < (students != null ? students.length() : 0); i++) {
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
            for (ProfileDownloadListener listener:profileDownloadListeners
                 ) {
                listener.onProfileDownload(allProfiles, schoolProfile);
            }

        }
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public interface ProfileDownloadListener {
        void onProfileDownload(List<RoleProfile> profiles, SchoolProfile schoolProfile);
    }
}
