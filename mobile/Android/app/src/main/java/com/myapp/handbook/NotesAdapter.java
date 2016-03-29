package com.myapp.handbook;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by SAshutosh on 3/23/2016.
 */
public class NotesAdapter extends CursorAdapter {

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {

        public final TextView titleView;
        public final TextView detailMsgView;
        public final TextView notificationIdView;
        public final TextView dateView;

        public ViewHolder(View view) {

            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            detailMsgView = (TextView) view.findViewById(R.id.list_item_msgdetail_textview);
            notificationIdView = (TextView) view.findViewById(R.id.list_item_noteid_textview);
            titleView = (TextView) view.findViewById(R.id.list_item_title_textview);
        }
    }



    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @param flags   Flags used to determine the behavior of the adapter; may
     *                be any combination of {@link #FLAG_AUTO_REQUERY} and
     *                {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public NotesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {


        String message=cursor.getString(5) + "\t" + cursor.getString(3)+"\n" + cursor.getString(4);
        return message;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_notes, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

      //  TextView tv = (TextView)view;
      //  tv.setText(convertCursorRowToUXFormat(cursor));
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.titleView.setText(cursor.getString(5));
        viewHolder.notificationIdView.setText(cursor.getString(1));
        viewHolder.dateView.setText(cursor.getString(3));
        viewHolder.detailMsgView.setText(cursor.getString(4));


    }
}
