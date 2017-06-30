package com.myapp.handbook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.data.HandbookContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotesDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotesDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private  long message_id;
    private SQLiteDatabase db;
    private Cursor cursor;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesDetailFragment newInstance(String param1, String param2) {
        NotesDetailFragment fragment = new NotesDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NotesDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        message_id = intent.getLongExtra("ID", 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes_detail,container,false);
        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());
        db = handbookDbHelper.getReadableDatabase();

        cursor= db.query(HandbookContract.NotificationEntry.TABLE_NAME,
                null,
                "_id= ?", new String[] {Long.toString(message_id)}, null, null, null, null);
        if(cursor.moveToFirst()){
            //int id = cursor.getInt(0);
           // int notificationId = cursor.getInt(0);
            String title = cursor.getString(5);
            String detail = cursor.getString(4);
            String date = cursor.getString(3);
            String from = cursor.getString(6);
            int priority = cursor.getInt(2);
            String imageUrl = cursor.getString(7);
            TextView titleTextView = (TextView)view.findViewById(R.id.detail_header);
            TextView detailTextView = (TextView)view.findViewById(R.id.detail_message);
            //TextView priorityTextView = (TextView)view.findViewById(R.id.detail_priority);
            TextView dateTextView = (TextView)view.findViewById(R.id.detail_date);
            TextView fromTextView = (TextView)view.findViewById(R.id.detail_from);
            ImageView imageDetailView = (ImageView) view.findViewById(R.id.detail_image);
            titleTextView.setText(title);
            detailTextView.setText(detail);
            //priorityTextView.setText(Integer.toString(priority));
            dateTextView.setText(date);
            fromTextView.setText(from);
            if(imageUrl!=null && !imageUrl.isEmpty()){
                Glide.with(getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.contact_picture_placeholder)
                        .error(R.drawable.contact_picture_error)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageDetailView);
            }
            else {
                imageDetailView.setVisibility(View.INVISIBLE);
            }


        }

//        View v=  inflater.inflate(R.layout.fragment_notes_detail, container, false);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
           // mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
