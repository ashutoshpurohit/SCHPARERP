package com.myapp.handbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.myapp.handbook.R;
import com.myapp.handbook.TimeTableActivity;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.TimeSlots;

import java.util.List;

/**
 * Created by SAshutosh on 10/16/2016.
 */

public class TimeTableSummaryAdapter extends RecyclerView.Adapter<TimeTableSummaryAdapter.TimeTableViewHolder> implements View.OnClickListener {

    private final List<TimeSlots> timeslots;

    private final Context context;

    private final RoleProfile.ProfileRole profileRole;

    public TimeTableSummaryAdapter(Context context, List<TimeSlots> slots, RoleProfile.ProfileRole role)
    {
        this.context = context;
        this.timeslots=slots;
        this.profileRole = role;
    }




    private String getSubjectInitials(String subject) {
        return subject.substring(0,3);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context ,TimeTableActivity.class);
        //Can pass student/teacherid from here
        //intent.putExtra("ID",rowId);
        context.startActivity(intent);
    }



    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public TimeTableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_timetablesummary_item,parent,false);
        itemView.setOnClickListener(this);
        return new TimeTableViewHolder(itemView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(TimeTableViewHolder holder, int position) {
        TimeSlots currentSlot = timeslots.get(position);
        holder.subject.setText(getSubjectInitials(currentSlot.getSubject()));
        holder.timeSlot.setText(currentSlot.getStartTime());
        if(profileRole.equals(RoleProfile.ProfileRole.TEACHER)){
            holder.std.setVisibility(View.VISIBLE);
            holder.std.setText(currentSlot.getTeacherClassStd());
        }
        else if(profileRole.equals(RoleProfile.ProfileRole.STUDENT)){
            holder.std.setVisibility(View.GONE);
        }
        holder.subject_std_container.refreshDrawableState();
        holder.subject_std_container.setBackgroundColor(getColor(position));
    }

    private int getColor(int position) {
        int result = position%3;
        if(result==0)
            return Color.RED;
        else if(result==1)
            return Color.GREEN;
        else
            return Color.BLUE;
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return timeslots.size();
    }

    public class TimeTableViewHolder extends RecyclerView.ViewHolder{
        TextView timeSlot, subject, std;
        View subject_std_container;


        public TimeTableViewHolder(View itemView) {
            super(itemView);
            timeSlot= (TextView)itemView.findViewById(R.id.timetable_summary_duration);
            subject= (TextView)itemView.findViewById(R.id.timetable_summary_subject);
            std = (TextView)itemView.findViewById(R.id.timetable_summary_std);
            subject_std_container = itemView.findViewById(R.id.timetable_summary_subject_std_holder);
            //itemView.setOnClickListener((View.OnClickListener) this);

        }
    }
}

