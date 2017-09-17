package com.myapp.handbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.myapp.handbook.Listeners.RecycleViewClickListener;
import com.myapp.handbook.NotesDetailActivity;
import com.myapp.handbook.R;

import static android.content.ContentValues.TAG;
import static com.myapp.handbook.HttpConnectionUtil.DIARY_NOTE_TYPE;
import static com.myapp.handbook.HttpConnectionUtil.HOMEWORK_TYPE;
import static com.myapp.handbook.HttpConnectionUtil.PARENT_NOTE_TYPE;

/**
 * Created by SAshutosh on 7/19/2016.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener
 {

     public SparseBooleanArray selectedItems;
    // Because RecyclerView.Adapter in its current form doesn't natively
    // support cursors, we wrap a CursorAdapter that will do all the job
    // for us.
     CursorAdapter mCursorAdapter;
     Context mContext;
     AppCompatActivity activity;
     Toolbar toolbar;
     RecycleViewClickListener itemClickListener;

     ActionMode.Callback notesContext;
     long dbId;
     ActionMode node;
     private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
         @Override
         public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
             // todo log exception
             Log.e(TAG, "onException:Glide exception ", e);

             // important to return false so the error placeholder can be placed
             return false;
         }

         @Override
         public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
             return false;
         }
     };


     public MyRecyclerAdapter(Context context, Cursor c, RecycleViewClickListener listener) {

         this.mContext = context;

         this.mCursorAdapter = new CursorAdapter(mContext, c, 0) {

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
                //viewHolder.fromMsgView.setText(cursor.getString(6));
                viewHolder.dateView.setText(cursor.getString(3));
                viewHolder.detailMsgView.setText(cursor.getString(4));
                viewHolder.dbId=cursor.getLong(0);
                int msgType = cursor.getInt(8);
                if(msgType == DIARY_NOTE_TYPE){
                    viewHolder.fromMsgView.setText("Diary Note");
                    //viewHolder.msgTypeIcon.setImageDrawable( ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_view_list, null));
                }
                else if(msgType ==HOMEWORK_TYPE){
                    //viewHolder.msgTypeIcon.setImageDrawable( ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_create, null));
                    viewHolder.fromMsgView.setText("Homework");
                }
                else if(msgType==PARENT_NOTE_TYPE){
                    viewHolder.fromMsgView.setText("Parent message");
                }

                viewHolder.position=cursor.getPosition();
                String imageUrl = cursor.getString(7);

                if(imageUrl!=null && !imageUrl.isEmpty()){
                    imageUrl = checkImageUrl(imageUrl);

                    Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.contact_picture_placeholder)
                            .listener( requestListener )
                            .error(R.drawable.contact_picture_error)
                            .override(120,120)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(viewHolder.imageView);
                }
                else {
                    viewHolder.imageView.setImageDrawable(null);
                }
            }
        };

         this.selectedItems = new SparseBooleanArray();

         this.itemClickListener = listener;

    }

     public void setNotesContext(ActionMode.Callback notesContext) {
         this.notesContext = notesContext;
     }

     public void setActivity(AppCompatActivity activity) {
         this.activity = activity;
     }

     public void setToolbar(Toolbar toolbar) {
         this.toolbar = toolbar;
     }

     public long getDbId() {
         return dbId;
     }

     public void setDbId(long dbId) {
         this.dbId = dbId;
     }

     //Remove and add to image url for progressive loading of images
     public String checkImageUrl(String str){

         String updatedStr;
         String str1 = str.substring(0,50);
         String str2 = str.substring(61);
         updatedStr = str1 + "w_0.5,h_0.5,c_fit" +str2;
         return updatedStr;
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
            //TextView fromMsgView = (TextView) v.findViewById(R.id.list_item_noteid_textview);
            //long rowId = Integer.parseInt(fromMsgView.getText().toString());
            ViewHolder viewHolder = (ViewHolder) v.getTag();
            if (viewHolder != null)
                rowId = viewHolder.dbId;

            //Check if the row is already selected de-select it
            if (selectedItems.get((int)rowId, false)) {
                selectedItems.delete((int)rowId);
                v.setSelected(false);
                return;
            }

            if (v.getId() == R.id.list_item_msg_type_icon) {
                Toast.makeText(mContext, "File download clicked", Toast.LENGTH_LONG);
            } else {

                Intent intent = new Intent(mContext, NotesDetailActivity.class);
                intent.putExtra("ID", rowId);
                mContext.startActivity(intent);
            }
            //TO-DO Add the current fragment to back stack so that back button works
        }

    }

     /**
      * Called when a view has been clicked and held.
      *
      * @param v The view that was clicked and held.
      * @return true if the callback consumed the long click, false otherwise.
      */
     @Override
     public boolean onLongClick(View v) {
         long rowId =0;
         long adapterPosition =0;
         if(v!=null){
             ViewHolder viewHolder = (ViewHolder) v.getTag();
             rowId = viewHolder.dbId;
             adapterPosition = viewHolder.position;                     //viewHolder.getAdapterPosition();
         }
         if (selectedItems.get((int)adapterPosition, false)) {
             selectedItems.delete((int)adapterPosition);
             v.setSelected(false);
         }
         else {
             selectedItems.put((int)adapterPosition, true);
             v.setSelected(true);
             setDbId(rowId);
             //Toolbar toolbar = (Toolbar) v.findViewById(R.id.my_toolbar);
             activity.startSupportActionMode(notesContext);
             //toolbar.startActionMode()
             //node =activity.startActionMode(notesContext);
             //startSupportActionMode
             //toolbar.startActionMode(notesContext);
         }

         return true;
     }

     @Override
     public int getItemCount() {
         return mCursorAdapter.getCount();
     }

     @Override
     public void onBindViewHolder(ViewHolder holder, int position) {
         // Passing the binding operation to cursor loader
         //holder.position=position;
         mCursorAdapter.getCursor().moveToPosition(position);
         mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());

     }

     @Override
     public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         // Passing the inflater job to the cursor-adapter
         View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
         v.setOnClickListener(this);
         v.setOnLongClickListener(this);
         ImageView fileDownloadIcon = (ImageView) v.findViewById(R.id.list_item_msg_type_icon);
         fileDownloadIcon.setOnClickListener(this);
         return new ViewHolder(v);
     }

     public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView titleView;
        public final TextView detailMsgView;
        public final TextView fromMsgView;
        public final TextView dateView;
        public final ImageView imageView;
         public final ImageView fileDownloadIcon;
        public Long dbId;
        public int position;
         View v1;
        public ViewHolder(View view) {

            super(view);
            //
            //view.setOnLongClickListener(this);

            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            detailMsgView = (TextView) view.findViewById(R.id.list_item_msgdetail_textview);
            fromMsgView = (TextView) view.findViewById(R.id.list_item_note_from_textview);
            titleView = (TextView) view.findViewById(R.id.list_item_title_textview);
            imageView =(ImageView)view.findViewById(R.id.list_item_notes_image);
            fileDownloadIcon = (ImageView) view.findViewById(R.id.list_item_msg_type_icon);
            fileDownloadIcon.setOnClickListener(this);
            //position=this.getAdapterPosition();
            view.setOnClickListener(this);
        }


         /**
          * Called when a view has been clicked.
          *
          * @param v The view that was clicked.
          */
         @Override
         public void onClick(View v) {

             int rowId = 0;
             Log.d("Adapter", "onClick:Called ");
             MyRecyclerAdapter.ViewHolder viewHolder = (MyRecyclerAdapter.ViewHolder) v.getTag();
             if (viewHolder != null)
                 rowId = (int) (long) viewHolder.dbId;

             if (itemClickListener != null)
                 itemClickListener.recyclerViewClicked(v, rowId);

         }

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
}