package com.myapp.handbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.myapp.handbook.R;
import com.myapp.handbook.domain.TimeSlots;

import java.util.List;

/**
 * Created by SAshutosh on 10/16/2016.
 */

public class TimeTableAdapter extends ArrayAdapter<TimeSlots> {
    private final Context context;
    private final int layoutResourceId;
    private final List<TimeSlots> timeslots;

    /**
     * Constructor
     *
     * @param context   The current context.
     * @param resource  The resource ID for a layout file containing a TextView to use when
     *                  instantiating views.
     * @param timeSlots The objects to represent in the ListView.
     */
    public TimeTableAdapter(Context context, int resource, List<TimeSlots> timeSlots) {
        super(context, resource, timeSlots);

        this.context = context;
        this.layoutResourceId = resource;
        this.timeslots = timeSlots;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View currentRow = convertView;
        if (currentRow == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            currentRow = inflater.inflate(layoutResourceId, parent, false);

        }
        TimeSlots currentTimeSlot = timeslots.get(position);

        TextView timeSlot = (TextView) currentRow.findViewById(R.id.timetable_duration);
        TextView subject = (TextView) currentRow.findViewById(R.id.timetable_subject);

        timeSlot.setText(currentTimeSlot.getCurrentTimeSlot());
        subject.setText(currentTimeSlot.getSubject());
        return currentRow;
    }
}

