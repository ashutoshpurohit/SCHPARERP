package com.myapp.handbook;

import android.support.v4.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.data.HandbookContract;

public class NotesFragment extends Fragment {

    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());
        db = handbookDbHelper.getReadableDatabase();

        cursor= db.query(HandbookContract.NotificationEntry.TABLE_NAME,
                null,
                null, null, null, null, null, null);

        /* CursorAdapter listAdapter = new SimpleCursorAdapter(inflater.getContext(),
                android.R.layout.simple_list_item_1,
                cursor,new String[]{HandbookContract.NotificationEntry.COLUMN_TITLE},new int[]{android.R.id.text1},0 );*/
        NotesAdapter listAdapter = new NotesAdapter(inflater.getContext(),cursor,0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_notes);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor!=null){
                    long rowId = cursor.getLong(0);
                    Intent intent = new Intent(getActivity(),NotesDetailActivity.class);
                    intent.putExtra("ID",rowId);
                    startActivity(intent);
                }

            }
        });
        return rootView;

        //setListAdapter(listAdapter);



        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                                inflater.getContext(),
                                                                android.R.layout.simple_list_item_1,
                                                                Notifications.notes);
        setListAdapter(adapter);*/
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
