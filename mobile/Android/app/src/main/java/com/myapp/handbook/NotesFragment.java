package com.myapp.handbook;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;

import com.myapp.handbook.adapter.MyRecyclerAdapter;
import com.myapp.handbook.adapter.NotesAdapter;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.data.HandbookContract;

public class NotesFragment extends Fragment {

    private SQLiteDatabase db;
    private Cursor cursor;
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());

        db = handbookDbHelper.getReadableDatabase();

        cursor= db.query(HandbookContract.NotificationEntry.TABLE_NAME,
                null,
                null, null, null, null, null, null);

        /* CursorAdapter listAdapter = new SimpleCursorAdapter(inflater.getContext(),
                android.R.layout.simple_list_item_1,
                cursor,new String[]{HandbookContract.NotificationEntry.COLUMN_TITLE},new int[]{android.R.id.text1},0 );*/
        //NotesAdapter listAdapter = new NotesAdapter(inflater.getContext(),cursor,0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        /*ListView listView = (ListView) rootView.findViewById(R.id.listview_notes);
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
        });*/
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyRecyclerAdapter(this.getContext(),cursor);
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.set
        return rootView;

        //setListAdapter(listAdapter);



        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                                inflater.getContext(),
                                                                android.R.layout.simple_list_item_1,
                                                                Notifications.notes);
        setListAdapter(adapter);*/
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){

        super.onCreateOptionsMenu(menu,menuInflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            /**
             * Called when the user submits the query. This could be due to a key press on the
             * keyboard or due to pressing a submit button.
             * The listener can override the standard behavior by returning true
             * to indicate that it has handled the submit request. Otherwise return false to
             * let the SearchView handle the submission by launching any associated intent.
             *
             * @param query the query text that is to be submitted
             * @return true if the query has been handled by the listener, false to let the
             * SearchView perform the default action.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("NotesFragment", "Submitted query" + query);
                searchView.clearFocus();
                return true;
            }

            /**
             * Called when the query text is changed by the user.
             *
             * @param newText the new content of the query text field.
             * @return false if the SearchView should perform the default action of showing any
             * suggestions if available, true if the action was handled by the listener.
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
