package com.myapp.handbook.Tasks;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.myapp.handbook.HttpConnectionUtil;
import com.myapp.handbook.ServiceGenerator;
import com.myapp.handbook.domain.Event;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

/**
 * Created by SAshutosh on 1/14/2017.
 */

public class FetchSchoolCalendarAsyncTask extends AsyncTask<Void, Void, List<Event>> {

    List<CalendarDownloadedListener> postDownloadListener;
    private DownloadCallback mCallback;
    String schoolId;

    public FetchSchoolCalendarAsyncTask(List<CalendarDownloadedListener> postDownloadListener, String schoolId) {
        this.postDownloadListener = postDownloadListener;
        this.schoolId=schoolId;
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
    protected List<Event> doInBackground(Void... params) {
        HttpConnectionUtil.SchoolCalendarService schoolCalendarService= ServiceGenerator.
                createService(HttpConnectionUtil.SchoolCalendarService.class);
        Call<List<Event>> call= schoolCalendarService.getSchoolCalendar(schoolId);
        try {
            return call.execute().body();
        } catch (IOException e) {
            //To-DO add logging here
            return  null;
            //e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(List<Event> events) {
        for (CalendarDownloadedListener listener :postDownloadListener
                ) {
            listener.onFinished(events);
        }
    }

    public interface CalendarDownloadedListener {

        void onFinished(List<Event> events);
    }
}
