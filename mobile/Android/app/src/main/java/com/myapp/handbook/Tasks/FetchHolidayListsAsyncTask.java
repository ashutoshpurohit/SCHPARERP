package com.myapp.handbook.Tasks;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.myapp.handbook.HttpConnectionUtil;
import com.myapp.handbook.ServiceGenerator;
import com.myapp.handbook.domain.HolidayLists;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

/**
 * Created by SAshutosh on 1/14/2017.
 */

public class FetchHolidayListsAsyncTask extends AsyncTask<Void, Void, List<HolidayLists>> {

    List<HolidayListsDownloadedListener> postDownloadListener;
    String schoolId;
    private DownloadCallback mCallback;

    public FetchHolidayListsAsyncTask(List<HolidayListsDownloadedListener> postDownloadListener, String schoolId) {
        this.postDownloadListener = postDownloadListener;
        this.schoolId = schoolId;
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
    protected List<HolidayLists> doInBackground(Void... params) {
        HttpConnectionUtil.SchoolHolidayListsService schoolHolidayListsService = ServiceGenerator.
                createService(HttpConnectionUtil.SchoolHolidayListsService.class);
        Call<List<HolidayLists>> call = schoolHolidayListsService.getSchoolHolidayLists("501");
        try {
            return call.execute().body();
        } catch (IOException e) {
            //To-DO add logging here
            return null;
            //e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(List<HolidayLists> holidayList) {
        for (HolidayListsDownloadedListener listener : postDownloadListener
                ) {
            listener.onFinished(holidayList);
        }
    }

    public interface HolidayListsDownloadedListener {

        void onFinished(List<HolidayLists> holidayList);


    }
}
