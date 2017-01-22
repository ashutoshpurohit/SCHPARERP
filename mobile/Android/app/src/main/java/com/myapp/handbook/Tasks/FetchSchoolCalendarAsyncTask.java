package com.myapp.handbook.Tasks;

import android.os.AsyncTask;

import com.myapp.handbook.HttpConnectionUtil;
import com.myapp.handbook.ServiceGenerator;
import com.myapp.handbook.domain.CalendarEvents;
import com.myapp.handbook.domain.Event;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

/**
 * Created by SAshutosh on 1/14/2017.
 */

public class FetchSchoolCalendarAsyncTask extends AsyncTask<Void, Void, List<Event>> {


    public interface CalendarDownloadedListener {

        public void onFinished(List<Event> events);
    }

    List<CalendarDownloadedListener> postDownloadListener;

    public FetchSchoolCalendarAsyncTask(List<CalendarDownloadedListener> postDownloadListener) {
        this.postDownloadListener = postDownloadListener;
    }

    @Override
    protected List<Event> doInBackground(Void... params) {
        HttpConnectionUtil.SchoolCalendarService schoolCalendarService= ServiceGenerator.createService(HttpConnectionUtil.SchoolCalendarService.class);
        Call<List<Event>> call= schoolCalendarService.getSchoolCalendar();
        try {
            List<Event> schoolCalendar = call.execute().body();
            return  schoolCalendar;
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
}
