package com.myapp.handbook.Tasks;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.myapp.handbook.HttpConnectionUtil;
import com.myapp.handbook.ServiceGenerator;
import com.myapp.handbook.domain.BaseTimeTable;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.TeacherTimeTable;
import com.myapp.handbook.domain.TimeTable;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

/**
 * Created by SAshutosh on 12/17/2016.
 */

public class FetchTimeTableAsyncTask extends AsyncTask<Void, Void, BaseTimeTable> {
    final List<TaskListener> timeTableListeners;
    RoleProfile currentProfile;
    private DownloadCallback mCallback;
    public FetchTimeTableAsyncTask(RoleProfile profile, List<TaskListener> listeners) {
        this.currentProfile =profile;
        this.timeTableListeners =listeners;
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
    }

    @Override
    protected BaseTimeTable doInBackground(Void... params)
    {
        HttpConnectionUtil.TimeTableService timeTableService = ServiceGenerator.createService(HttpConnectionUtil.TimeTableService.class);

        Call<TimeTable> call=null;
        Call<TeacherTimeTable> teacherCall=null;
        BaseTimeTable timeTable =null;
        try
        {
            if(currentProfile.getRole().equals( RoleProfile.ProfileRole.STUDENT.toString()))
            {
                call = timeTableService.getStudentTimeTable(currentProfile.getId());
                timeTable = call.execute().body();
            }
            else if (currentProfile.getRole().equals(RoleProfile.ProfileRole.TEACHER.toString()))
            {
                teacherCall = timeTableService.getTeacherTimeTable(currentProfile.getId());
                timeTable = teacherCall.execute().body();
            }
            return timeTable;
        }
        catch (IOException e)
        {
            Log.d("FetchTimeTableAsyncTask", "Error in fetching school profile");
        }

        return null;
    }

    @Override
    protected void onPostExecute(BaseTimeTable timeTable) {
        //profileTimeTable =timeTable;

        for (TaskListener listener:timeTableListeners
                ) {
            listener.onFinished(timeTable);

        }

    }

    public interface TaskListener {
        void onFinished(BaseTimeTable table);
    }

}