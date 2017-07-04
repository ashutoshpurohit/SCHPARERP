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
    private final String profileRole;
    private TimeTableViewType viewType;

    /**
     * Constructor
     *
     * @param context   The current context.
     * @param resource  The resource ID for a layout file containing a TextView to use when
     *                  instantiating views.
     * @param timeSlots The objects to represent in the ListView.
     */
    public TimeTableAdapter(Context context, int resource, List<TimeSlots> timeSlots, String profileRole) {
        super(context, resource, timeSlots);

        this.context = context;
        this.layoutResourceId = resource;
        this.timeslots = timeSlots;

        viewType=TimeTableViewType.DETAIL;
        this.profileRole = profileRole;
    }

    public void setType(TimeTableViewType type)
    {
        viewType=type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View currentRow = convertView;
        if (currentRow == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            currentRow = inflater.inflate(layoutResourceId, parent, false);

        }

        TimeSlots currentTimeSlot = timeslots.get(position);


        if(viewType==TimeTableViewType.DETAIL) {
            TextView timeSlot = (TextView) currentRow.findViewById(R.id.timetable_duration);
            TextView subject = (TextView) currentRow.findViewById(R.id.timetable_subject);
            TextView standard = (TextView) currentRow.findViewById(R.id.timetable_teacher_std);

               if (profileRole.equals("TEACHER")) {

                   timeSlot.setText(currentTimeSlot.getCurrentTimeSlot());
                   subject.setText(currentTimeSlot.getSubject());
                   standard.setVisibility(View.VISIBLE);
                   standard.setText(currentTimeSlot.getTeacherClassStd());
               }else {
                   standard.setVisibility(View.GONE);
                   timeSlot.setText(currentTimeSlot.getCurrentTimeSlot());
                   subject.setText(currentTimeSlot.getSubject());
               }

        }
        else {
            TextView timeSlot = (TextView) currentRow.findViewById(R.id.timetable_summary_duration);
            TextView subject = (TextView) currentRow.findViewById(R.id.timetable_summary_subject);
            timeSlot.setText(getStartTime(currentTimeSlot.getCurrentTimeSlot()));
            subject.setText(getSubjectInitials(currentTimeSlot.getSubject()));
            }
        return currentRow;

    }

    private String getSubjectInitials(String subject) {
        return subject.substring(0,3);
    }

    private String getStartTime(String currentTimeSlot) {

         int index= currentTimeSlot.indexOf('-');
        String startTime= currentTimeSlot.substring(0,4);
        if(index >1)
            currentTimeSlot.substring(0,index-1);
        return startTime;
    }
}

