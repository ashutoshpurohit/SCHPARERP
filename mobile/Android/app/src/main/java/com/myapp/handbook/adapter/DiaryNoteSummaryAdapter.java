package com.myapp.handbook.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.myapp.handbook.HttpConnectionUtil;
import com.myapp.handbook.NotesActivity;
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

    public DiaryNoteSummaryAdapter(Activity activity,List<DiaryNote> notes)
    {
        this.parentActivity = activity;
        this.diaryNotes=notes;
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
    public DiaryNoteSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_diarynotesummary_item,parent,false);
        itemView.setOnClickListener(this);
        return new DiaryNoteSummaryViewHolder(itemView);
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
    public void onBindViewHolder(DiaryNoteSummaryViewHolder holder, int position) {
        DiaryNote currentNote = diaryNotes.get(position);
        holder.title.setText(currentNote.getTitle());
        holder.msg_detail.setText(getSummaryNote(currentNote.getDetail()));
        holder.date.setText(currentNote.getDate());
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

        /*Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("position", 1);
        context.startActivity(intent);*/

        Intent intent = new Intent(parentActivity.getApplicationContext(), NotesActivity.class);
        parentActivity.startActivity(intent);

       /* Fragment fragment = new DiaryNotesFragment();

        MainActivity mainActivity = (MainActivity)parentActivity;
        android.support.v4.app.FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
        //ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.replace(R.id.frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commitAllowingStateLoss();*/
        //Set the action bar title

    }

    public class DiaryNoteSummaryViewHolder extends RecyclerView.ViewHolder{
        TextView title, date, msg_detail;
        ImageView msg_img;
        ImageView msg_attchment;


        public DiaryNoteSummaryViewHolder(View itemView) {
            super(itemView);
            title= (TextView)itemView.findViewById(R.id.diaryNote_summary_title);
            date= (TextView)itemView.findViewById(R.id.diaryNote_summary_date);
            msg_detail=(TextView)itemView.findViewById(R.id.diaryNote_summary_msg);
            msg_img = (ImageView)itemView.findViewById(R.id.diaryNote_msg_image);
            msg_attchment=(ImageView) itemView.findViewById(R.id.diaryNote_msg_attachment);
        }
    }
}

