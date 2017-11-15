package com.myapp.handbook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myapp.handbook.R;
import com.myapp.handbook.domain.HolidayLists;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by SAshutosh on 1/17/2017.
 */

public class HolidayListsAdapter extends RecyclerView.Adapter<HolidayListsAdapter.HolidayListsViewHolder> {

    private final Context context;

    List<HolidayLists> holidayLists;


    public HolidayListsAdapter(Context context, List<HolidayLists> holidayLists) {
        this.holidayLists = holidayLists;
        this.context = context;

    }

    @Override
    public HolidayListsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_holiday_item, parent, false);

       /* itemView.setOnClickListener(this);*/
        return new HolidayListsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HolidayListsViewHolder holder, int position) {

        try {

            Date holidayDate = new Date();
            HolidayLists currentHolidayLists = holidayLists.get(position);
            String currentHolidayDate = currentHolidayLists.getHolidayDate();
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                SimpleDateFormat ddmmyyFormat = new SimpleDateFormat("dd/MM/yy");

                if (currentHolidayDate != null) {
                    holidayDate = df.parse(currentHolidayDate);
                }
                holder.holidayDate.setText(ddmmyyFormat.format(holidayDate));
            } catch (ParseException | RuntimeException e) {
                e.printStackTrace();
                holder.holidayDate.setText(currentHolidayDate);
            }
            holder.holidayName.setText(currentHolidayLists.getHoliday());
            /*holder.eventLocation.setText(currentEvent.getEventPlace());
            holder.eventTimings.setText(currentEvent.getEventStartTime() + "-" + currentEvent.getEventEndTime());
        */
        } catch (Exception ex) {
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
        return holidayLists.size();
    }


    public class HolidayListsViewHolder extends RecyclerView.ViewHolder {
        TextView holidayName, holidayDescription, holidayDate, holidayType;


        public HolidayListsViewHolder(View itemView) {
            super(itemView);
            holidayName = (TextView) itemView.findViewById(R.id.holiday_name);
            //holidayDescription = (TextView)itemView.findViewById(R.id.holiday_description);
            holidayDate = (TextView) itemView.findViewById(R.id.holiday_date);
            // holidayType = (TextView)itemView.findViewById(R.id.holiday_type);
        }

    }
}
