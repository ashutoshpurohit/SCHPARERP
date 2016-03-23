package com.myapp.handbook;

import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.data.HandbookContract;

public class PizzaFragment extends ListFragment {

    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());
        db = handbookDbHelper.getReadableDatabase();

        cursor= db.query(HandbookContract.NotificationEntry.TABLE_NAME,
                new String[]{"_id", HandbookContract.NotificationEntry.COLUMN_TITLE},
                null, null, null, null, null, null);

        CursorAdapter listAdapter = new SimpleCursorAdapter(inflater.getContext(),android.R.layout.simple_list_item_1,
                cursor,new String[]{HandbookContract.NotificationEntry.COLUMN_TITLE},new int[]{android.R.id.text1},0 );
        setListAdapter(listAdapter);
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                                inflater.getContext(),
                                                                android.R.layout.simple_list_item_1,
                                                                Notifications.notes);
        setListAdapter(adapter);*/
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
