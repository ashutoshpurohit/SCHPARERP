package com.myapp.handbook.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.handbook.HttpConnectionUtil;
import com.myapp.handbook.NotesActivity;
import com.myapp.handbook.NotesDetailActivity;
import com.myapp.handbook.R;
import com.myapp.handbook.domain.DiaryNote;

import java.util.List;

/**
 * Created by SAshutosh on 10/16/2016.
 */

public class DiaryNoteSummaryAdapter extends RecyclerView.Adapter<DiaryNoteSummaryAdapter.DiaryNoteSummaryViewHolder> implements View.OnClickListener{

    private final List<DiaryNote> diaryNotes;
   // private final Context context;
    private final Activity parentActivity;
    private final int messageType;
    long msgId;

    public DiaryNoteSummaryAdapter(Activity activity, List<DiaryNote> notes, int messageType)
    {
        this.parentActivity = activity;
        this.diaryNotes=notes;
        this.messageType = messageType;
    }


    @Override
    public DiaryNoteSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_diarynotesummary_item,parent,false);
        itemView.setOnClickListener(this);
        return new DiaryNoteSummaryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DiaryNoteSummaryViewHolder holder, int position) {
        DiaryNote currentNote = diaryNotes.get(position);
        holder.title.setText(currentNote.getTitle());
        holder.msg_detail.setText(getSummaryNote(currentNote.getDetail()));
        holder.date.setText(currentNote.getDate());
        // holder.msg_id.setText( String.valueOf(currentNote.getDbRowId()));
        holder.Id = currentNote.getDbRowId();


        //msgId = currentNote.getDbRowId();

        String chkImage = currentNote.getImage_url();
        if( chkImage == null || chkImage.isEmpty()){
            holder.msg_img.setVisibility(View.GONE);
            holder.msg_attchment.setVisibility(View.GONE);
        }
        else {
            if(HttpConnectionUtil.isImage(chkImage)){
                holder.msg_img.setVisibility(View.VISIBLE);
            }
            else {
                holder.msg_attchment.setVisibility(View.VISIBLE);
            }
        }

    }

    private String getSummaryNote(String detail) {
        if(detail.length() >100)
            return detail.substring(0,100)+ "..";
        else
            return  detail;
    }


    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return diaryNotes.size();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        Intent intent = new Intent(parentActivity.getApplicationContext(), NotesActivity.class);
        intent.putExtra(NotesActivity.MESSAGE_TYPE, this.messageType);
        parentActivity.startActivity(intent);
    }

    public class DiaryNoteSummaryViewHolder extends RecyclerView.ViewHolder{
        TextView title, date, msg_detail;
        ImageView msg_img;
        ImageView msg_attchment;
        TextView msg_id;
        long Id;

        public DiaryNoteSummaryViewHolder(View itemView) {
            super(itemView);
            title= (TextView)itemView.findViewById(R.id.diaryNote_summary_title);
            date= (TextView)itemView.findViewById(R.id.diaryNote_summary_date);
            msg_detail=(TextView)itemView.findViewById(R.id.diaryNote_summary_msg);
            msg_img = (ImageView)itemView.findViewById(R.id.diaryNote_msg_image);
            msg_attchment=(ImageView) itemView.findViewById(R.id.diaryNote_msg_attachment);
            // msg_id = (TextView)itemView.findViewById(R.id.dbRowId);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    if (pos != RecyclerView.NO_POSITION) {
                        DiaryNote clickedDataItem = diaryNotes.get(pos);
                        Id = clickedDataItem.getDbRowId();
                        Intent intent = new Intent(parentActivity.getApplicationContext(), NotesDetailActivity.class);
                        intent.putExtra("ID", Id);

                        parentActivity.startActivity(intent);
                        //Toast.makeText(v.getContext(), "You clicked " + clickedDataItem.getDbRowId(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
}

