package com.myapp.handbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.handbook.NotesDetailActivity;
import com.myapp.handbook.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by SAshutosh on 7/19/2016.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener
 {

    // Because RecyclerView.Adapter in its current form doesn't natively
    // support cursors, we wrap a CursorAdapter that will do all the job
    // for us.
     CursorAdapter mCursorAdapter;
     private SparseBooleanArray selectedItems;
     Context mContext;
     Activity activity;

     ActionMode.Callback notesContext;

     public void setNotesContext(ActionMode.Callback notesContext) {
         this.notesContext = notesContext;
     }



     public void setActivity(Activity activity) {
         this.activity = activity;
     }



     public long getDbId() {
         return dbId;
     }

     public void setDbId(long dbId) {
         this.dbId = dbId;
     }

     long dbId;
    public MyRecyclerAdapter(Context context, Cursor c) {

        mContext = context;

        mCursorAdapter = new CursorAdapter(mContext, c, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                // Inflate the view here
                View view = LayoutInflater.from(context).inflate(R.layout.list_item_notes, parent, false);
                ViewHolder viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                // Binding operations
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                viewHolder.titleView.setText(cursor.getString(5));
                viewHolder.notificationIdView.setText(cursor.getString(1));
                viewHolder.dateView.setText(cursor.getString(3));
                viewHolder.detailMsgView.setText(cursor.getString(4));
                viewHolder.dbId=cursor.getLong(0);
                String imageUrl = cursor.getString(7);
                if(imageUrl!=null && !imageUrl.isEmpty()){
                    Picasso.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.contact_picture_placeholder)
                            .error(R.drawable.contact_picture_error)
                            .resize(200,200)
                            .into(viewHolder.imageView);
                }
                else {
                    viewHolder.imageView.setVisibility(View.INVISIBLE);
                }
            }
        };

        selectedItems= new SparseBooleanArray();


    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        long rowId =0;
        //Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if(v!=null){
            //long rowId = cursor.getLong(0);
            //long rowId = v.getId();
            //TextView notificationIdView = (TextView) v.findViewById(R.id.list_item_noteid_textview);
            //long rowId = Integer.parseInt(notificationIdView.getText().toString());
            ViewHolder viewHolder = (ViewHolder) v.getTag();
            rowId = viewHolder.dbId;

            //Check if the row is already selected de-select it
            if (selectedItems.get((int)rowId, false)) {
                selectedItems.delete((int)rowId);
                v.setSelected(false);
                return;
            }

            Intent intent = new Intent(mContext ,NotesDetailActivity.class);
            intent.putExtra("ID",rowId);
            mContext.startActivity(intent);
            //TO-DO Add the current fragment to back stack so that back button works
        }

    }

     ActionMode node;

     /**
      * Called when a view has been clicked and held.
      *
      * @param v The view that was clicked and held.
      * @return true if the callback consumed the long click, false otherwise.
      */
     @Override
     public boolean onLongClick(View v) {
         long rowId =0;
         if(v!=null){
             ViewHolder viewHolder = (ViewHolder) v.getTag();
             rowId = viewHolder.dbId;
         }
         if (selectedItems.get((int)rowId, false)) {
             selectedItems.delete((int)rowId);
             v.setSelected(false);
         }
         else {
             selectedItems.put((int)rowId, true);
             v.setSelected(true);
             setDbId(rowId);
             //Toolbar toolbar = (Toolbar) v.findViewById(R.id.my_toolbar);

             node =activity.startActionMode(notesContext);
             //toolbar.startActionMode(notesContext);
         }

         return true;
     }


     public static class ViewHolder extends RecyclerView.ViewHolder  {
        View v1;
        public final TextView titleView;
        public final TextView detailMsgView;
        public final TextView notificationIdView;
        public final TextView dateView;
        public final ImageView imageView;
        public Long dbId;
        public ViewHolder(View view) {

            super(view);
            //view.setOnClickListener(this);
            //view.setOnLongClickListener(this);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            detailMsgView = (TextView) view.findViewById(R.id.list_item_msgdetail_textview);
            notificationIdView = (TextView) view.findViewById(R.id.list_item_noteid_textview);
            titleView = (TextView) view.findViewById(R.id.list_item_title_textview);
            imageView =(ImageView)view.findViewById(R.id.list_item_notes_image);
            //view.setOnCreateContextMenuListener(this);
        }


         /**
          * Called when a view has been clicked.
          *
          * @param v The view that was clicked.
          */
         /*@Override
         public void onClick(View v) {
             Log.d("Adapter", "onClick:Called ");
         }*/

         /**
          * Called when a view has been clicked and held.
          *
          * @param v The view that was clicked and held.
          * @return true if the callback consumed the long click, false otherwise.
          */
         /*@Override
         public boolean onLongClick(View v) {
             Log.d("Adapter", "onLongClick:Called ");
             return false;
         }*/
     }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Passing the binding operation to cursor loader
        mCursorAdapter.getCursor().moveToPosition(position); //EDITED: added this line as suggested in the comments below, thanks :)
        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Passing the inflater job to the cursor-adapter
        View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        return new ViewHolder(v);
    }
}