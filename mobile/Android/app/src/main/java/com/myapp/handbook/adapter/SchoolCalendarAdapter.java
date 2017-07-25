package com.myapp.handbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.handbook.CalendarEventsActivity;
import com.myapp.handbook.HttpConnectionUtil;
import com.myapp.handbook.Listeners.RecycleViewClickListener;
import com.myapp.handbook.R;
import com.myapp.handbook.TimeTableActivity;
import com.myapp.handbook.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by SAshutosh on 1/17/2017.
 */

public class SchoolCalendarAdapter extends RecyclerView.Adapter<SchoolCalendarAdapter.SchoolCalendarViewHolder> implements View.OnClickListener {

    private final Context context;
    private final HttpConnectionUtil.ViewType viewType;
    List<Event> calendarEvents;
    RecycleViewClickListener listener;

    public SchoolCalendarAdapter(Context context, List<Event> calendarEvents, HttpConnectionUtil.ViewType viewType, RecycleViewClickListener listener) {
        this.calendarEvents = calendarEvents;
        this.context = context;
        this.viewType = viewType;
        this.listener=listener;
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
    public SchoolCalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(this.viewType.equals(HttpConnectionUtil.ViewType.DETAIL)) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event, parent, false);
        }
        else {

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event_summary, parent, false);
        }
        itemView.setOnClickListener(this);
        return new SchoolCalendarViewHolder(itemView);
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
    public void onBindViewHolder(SchoolCalendarViewHolder holder, int position) {

        try {

            Date eventDate = new Date();
            Event currentEvent = calendarEvents.get(position);
            String currentEventDate = currentEvent.getEventDate();
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
                SimpleDateFormat ddmmyyFormat = new SimpleDateFormat("dd/MM/yy");

                if (currentEventDate != null) {
                    eventDate = df.parse(currentEventDate);
                }
                holder.eventDate.setText(ddmmyyFormat.format(eventDate));
            } catch (ParseException|RuntimeException e) {
                e.printStackTrace();
                holder.eventDate.setText(currentEventDate);
            }
            holder.eventName.setText(currentEvent.getEventName());
            holder.eventLocation.setText(currentEvent.getEventPlace());
            holder.eventTimings.setText(currentEvent.getEventStartTime() + "-" + currentEvent.getEventEndTime());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return calendarEvents.size();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.event_LikeButton:
                ImageButton likeButton =(ImageButton)v;
                v.setBackgroundResource(R.drawable.ic_favorite_fill);
                Toast.makeText(context,"Like pressed",Toast.LENGTH_SHORT ).show();
                break;
            case R.id.event_AddToCalendarButton:
                //addToCalendar();
                Toast.makeText(context,"Added to your calendar",Toast.LENGTH_SHORT ).show();
                break;*/
            default:
                if(viewType.equals(HttpConnectionUtil.ViewType.SUMMARY)) {
                    Intent intent = new Intent(context, CalendarEventsActivity.class);
                    //Can pass student/teacherid from here
                    //intent.putExtra("ID",rowId);
                    context.startActivity(intent);
                }
        }
    }

    public class  SchoolCalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView eventName, eventLocation, eventDate, eventTimings;
        ImageButton likeButton, addToCalendarButton;

        public SchoolCalendarViewHolder(View itemView) {
            super(itemView);
            eventName = (TextView)itemView.findViewById(R.id.event_name);
            eventLocation = (TextView)itemView.findViewById(R.id.event_location);
            eventDate = (TextView)itemView.findViewById(R.id.event_date);
            eventTimings = (TextView)itemView.findViewById(R.id.event_timing);
            likeButton= (ImageButton)itemView.findViewById(R.id.event_LikeButton);
            addToCalendarButton= (ImageButton) itemView.findViewById(R.id.event_AddToCalendarButton);
            if(viewType.equals(HttpConnectionUtil.ViewType.DETAIL)){
                likeButton.setOnClickListener(this);
                addToCalendarButton.setOnClickListener(this);
            }
            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if(listener!=null)
                listener.recyclerViewClicked(v,this.getLayoutPosition());
        }
    }
}
