package com.myapp.handbook;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.handbook.Listeners.RecycleViewClickListener;
import com.myapp.handbook.adapter.MyRecyclerAdapter;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.data.HandbookContract;
import com.myapp.handbook.util.AndroidPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.myapp.handbook.TeacherNoteFragment.TAG;

/**
 * Created by Ashutosh on 10/16/2017.
 */

public class HomeWorkNotesFragment extends Fragment implements RecycleViewClickListener {

    View view;
    RecyclerView mRecyclerView;
    ShareActionProvider shareActionProvider;
    Toolbar toolbar;
    String selectedProfileId = HttpConnectionUtil.getSelectedProfileId();

    boolean receiversRegistered = false;
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            //findViewById(R.id.start).setEnabled(true);
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                openDownloadedAttachment(getContext(), downloadId);
            }
        }
    };
    BroadcastReceiver onNotificationClick = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Downloading message", Toast.LENGTH_LONG).show();
        }
    };
    String formattedDate;
    Calendar c;
    SimpleDateFormat df;
    TextView current_date;
    TextView emptyRecyclerView;
    private SQLiteDatabase db;
    private Cursor cursor;
    private DownloadManager downloadManager = null;
    private long lastDownload = -1L;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ActionMode.Callback notesContext = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {


            toolbar.setVisibility(View.GONE);
            getActivity().getMenuInflater().inflate(R.menu.menu_notes_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            boolean status = false;
            MyRecyclerAdapter adapter = (MyRecyclerAdapter) mAdapter;
            long dbId = ((MyRecyclerAdapter) mAdapter).getDbId();
            switch (item.getItemId()) {
                case R.id.action_share_message:
                    //Check if item has image or is plain text
                    cursor = db.query(HandbookContract.NotificationEntry.TABLE_NAME,
                            null,
                            "_id= ?", new String[]{Long.toString(dbId)}, null, null, null, null);
                    if (cursor.moveToFirst()) {

                        int date = cursor.getInt(3);
                        String detail = cursor.getString(4);
                        String title = cursor.getString(5);
                        String from = cursor.getString(6);
                        String imageUrl = cursor.getString(7);
                        Intent i = new Intent(Intent.ACTION_SEND);
//                        if(imageUrl==null) {

                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_TEXT, prepareTextMessage(title, detail, from, date));
                        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.handbook_share_message));

//                        }
  /*                      else{

                            i.setType("image/*");
                            i.putExtra(Intent.EXTRA_STREAM,imageUrl);
                            //i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }*/


                        i = Intent.createChooser(i, getString(R.string.send_message));
                        startActivity(i);
                    }
                    mode.finish();
                    status = true;
                    Log.d(TAG, "onContextItemSelected: Share item called");
                    break;
                case R.id.action_delete:

                    //Code to delete the item
                    db.delete(HandbookContract.NotificationEntry.TABLE_NAME, HandbookContract.NotificationEntry._ID + "=" + dbId, null);

                    //Update the view
                    for (int i = 0; i < adapter.selectedItems.size(); i++) {
                        int key = adapter.selectedItems.keyAt(i);
                        if (adapter.selectedItems.get(key, false)) {
                            mAdapter.notifyItemRemoved(key);
                            adapter.selectedItems.delete(key);
                        }
                    }


                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT);
                    Log.d(TAG, "onContextItemSelected: Delete Item Called");
                    mode.finish();
                    status = true;
                    break;

                //case R.id.action_share_note:

            }

            return status;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            //Toolbar toolbar= (Toolbar)getActivity().findViewById(R.id.appTooolbar);
            toolbar.setVisibility(View.VISIBLE);

        }
    };

    public HomeWorkNotesFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

//        // Inflate the layout for this fragment
//        view = inflater.inflate(R.layout.content_calendar_events, container, false);
//
//          return view;
        downloadManager = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);
        if (receiversRegistered) {
            registerDownloadManagerIntentReceivers();
        }

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());
        db = handbookDbHelper.getReadableDatabase();
        view = inflater.inflate(R.layout.fragment_homework, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        /**
         * Code for next
         * and previous button
         *
         * Note that the month numbers are 0-based, so at the time of this writing (in April) the month number will be 3.
         * */

        // First of all to set current date to textview.
        //current_date = (TextView) view.findViewById(R.id.curDate);
        c = Calendar.getInstance();
        df = new SimpleDateFormat("dd/MM/yyyy");
        formattedDate = df.format(c.getTime());
        //current_date.setText(formattedDate);
        cursor = loadNotesFromDB(formattedDate, db);

        emptyRecyclerView = (TextView) view.findViewById(R.id.empty_view);
        ImageView img_prev_month = (ImageView) view.findViewById(R.id.img_month_previous);
        ImageView img_nxt_month = (ImageView) view.findViewById(R.id.img_month_next);

        img_nxt_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar tempDate = Calendar.getInstance();
                String tempFormattedDate = df.format(tempDate.getTime());
                if (formattedDate.equals(tempFormattedDate)) {
                    Toast.makeText(getContext(), "Homework not available for future dates", Toast.LENGTH_SHORT).show();
                } else {
                    c.add(Calendar.DATE, 1);
                    formattedDate = df.format(c.getTime());
                    //      current_date.setText(formattedDate);
                    cursor = loadNotesFromDB(formattedDate, db);

                }
                setupNotesAdapter();
            }
        });
        img_prev_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.add(Calendar.DATE, -1);
                formattedDate = df.format(c.getTime());
                //current_date.setText(formattedDate);
                cursor = loadNotesFromDB(formattedDate, db);
                setupNotesAdapter();
            }
        });

        setupNotesAdapter();


        return view;
    }

    private void setupNotesAdapter() {
        String testCursorData = DatabaseUtils.dumpCursorToString(cursor);
        Log.i("CursorDate", testCursorData);
        if (cursor != null && (cursor.getCount() > 0)) {
            if (View.VISIBLE == 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            mAdapter = new MyRecyclerAdapter(this.getContext(), cursor, this);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            ((MyRecyclerAdapter) mAdapter).setActivity((AppCompatActivity) getActivity());
            ((MyRecyclerAdapter) mAdapter).setNotesContext(notesContext);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            if (emptyRecyclerView != null)
                emptyRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private Cursor loadNotesFromDB(String selectedDate, SQLiteDatabase currentDB) {
        Cursor notesCursor = null;
        String query_to_fetch_earliest = "select *  from " + HandbookContract.NotificationEntry.TABLE_NAME + " where " +
                HandbookContract.NotificationEntry.COLUMN_MSG_TYPE + " = '" + HttpConnectionUtil.HOMEWORK_TYPE +
                "' and " + HandbookContract.NotificationEntry.COLUMN_TO_IDS + " LIKE " + "'%" + selectedProfileId +
                "%'" + " and " + HandbookContract.NotificationEntry.COLUMN_DATE + " = '" +
                selectedDate + "'";

        Log.i("query for notes", query_to_fetch_earliest);
        notesCursor = currentDB.rawQuery(query_to_fetch_earliest, null);
        return notesCursor;
    }


    private void registerDownloadManagerIntentReceivers() {
        getContext().registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        getContext().registerReceiver(onNotificationClick,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
        receiversRegistered = true;
    }

    private void unRegisterDownloadManagerIntentReceivers() {

        getContext().unregisterReceiver(onComplete);
        getContext().unregisterReceiver(onNotificationClick);
        receiversRegistered = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!receiversRegistered) {
            registerDownloadManagerIntentReceivers();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegisterDownloadManagerIntentReceivers();
    }

    private void openDownloadedAttachment(final Context context, final long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            String downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            String downloadMimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
            if ((downloadStatus == DownloadManager.STATUS_SUCCESSFUL) && downloadLocalUri != null) {
                openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType);
            }
        }
        cursor.close();
    }

    private void openDownloadedAttachment(final Context context, Uri attachmentUri, final String attachmentMimeType) {
        if (attachmentUri != null) {
            // Get Content Uri.
            if (ContentResolver.SCHEME_FILE.equals(attachmentUri.getScheme())) {
                // FileUri - Convert it to contentUri.
                File file = new File(attachmentUri.getPath());
                attachmentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".com.myapp.handbook.provider", file);
            }

            Intent openAttachmentIntent = new Intent(Intent.ACTION_VIEW);
            openAttachmentIntent.setDataAndType(attachmentUri, attachmentMimeType);
            openAttachmentIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(openAttachmentIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, context.getString(R.string.unable_to_open_file), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void recyclerViewClicked(View v, int position) {

        switch (v.getId()) {
            case R.id.list_item_file_name:
            case R.id.list_item_msg_type_icon:
                Toast.makeText(getContext(), "File download clicked", Toast.LENGTH_LONG);
                handleDownloadClick(position);
                break;
            default:
                Intent intent = new Intent(getContext(), NotesDetailActivity.class);
                intent.putExtra(NotesActivity.MESSAGE_TYPE, HttpConnectionUtil.HOMEWORK_TYPE);
                intent.putExtra("ID", position);
                getContext().startActivity(intent);

                break;
        }


    }

    private String prepareTextMessage(String title, String detail, String from, int date) {
        return "Message " + title + " date: " + date + " from: " + from + "\\n detail: " + detail;
    }

    private void handleDownloadClick(int message_id) {

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(getContext());
        String imageUrl = null;
        db = handbookDbHelper.getReadableDatabase();

        cursor = db.query(HandbookContract.NotificationEntry.TABLE_NAME,
                null,
                "_id= ?", new String[]{Long.toString(message_id)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            //int id = cursor.getInt(0);
            // int notificationId = cursor.getInt(0);
            String title = cursor.getString(5);
            String detail = cursor.getString(4);
            String date = cursor.getString(3);
            String from = cursor.getString(6);
            int priority = cursor.getInt(2);
            imageUrl = cursor.getString(7);
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            checkExternalStoragePermissionsAndDownload(imageUrl);
        }

    }


    private void checkExternalStoragePermissionsAndDownload(String downloadUrl) {
        if (AndroidPermissions.hasStoragePermissionGranted(getContext())) {
            //You can do what whatever you want to do as permission is granted
            startDownload(downloadUrl);
        } else {
            AndroidPermissions.requestExternalStoragePermission(getActivity());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == AndroidPermissions.REQUEST_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //User allow from permission dialog
                //You can do what whatever you want to do as permission is granted
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //User has deny from permission dialog
                Snackbar.make(getView(), "Please enable storage permission", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                ActivityCompat.requestPermissions(getActivity(), permissions, AndroidPermissions.REQUEST_STORAGE);
                            }
                        })
                        .show();
            } else {
                // User has deny permission and checked never show permission dialog so you can redirect to Application settings page
                Snackbar.make(getView(), "Please enable permission from settings", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    public void startDownload(String downloadUrl) {
        Uri uri = Uri.parse(downloadUrl);
        String fileName = HttpConnectionUtil.getFileNameFromUrl(downloadUrl);
        Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs();

        lastDownload =
                downloadManager.enqueue(new DownloadManager.Request(uri)
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE)
                        .setTitle("SchoolLink")
                        .setDescription(fileName)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                fileName));


    }


}
