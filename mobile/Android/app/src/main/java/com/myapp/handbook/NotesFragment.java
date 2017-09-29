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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.myapp.handbook.Listeners.RecycleViewClickListener;
import com.myapp.handbook.adapter.MyRecyclerAdapter;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.data.HandbookContract;
import com.myapp.handbook.util.AndroidPermissions;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class NotesFragment extends Fragment implements RecycleViewClickListener {

    private static final String TAG = "NotesFragment";
    RecyclerView mRecyclerView;
    ShareActionProvider shareActionProvider;
    Toolbar toolbar;
    String selectedProfileId = HttpConnectionUtil.getSelectedProfileId();
    String query_to_fetch_earliest="select *  from "+HandbookContract.NotificationEntry.TABLE_NAME+" where "+ HandbookContract.NotificationEntry.COLUMN_TO_IDS+" LIKE "+"'%"+selectedProfileId+"%'"  +" order  by datetime("+HandbookContract.NotificationEntry.COLUMN_TIMESTAMP+") DESC ";
    private SQLiteDatabase db;
    private Cursor cursor;
    private DownloadManager downloadManager=null;
    private long lastDownload=-1L;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    boolean receiversRegistered = false;
    private ActionMode.Callback notesContext = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {


            toolbar.setVisibility(View.GONE);
            getActivity().getMenuInflater().inflate(R.menu.menu_notes_context,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            boolean status=false;
            MyRecyclerAdapter adapter = (MyRecyclerAdapter)mAdapter;
            long dbId = ((MyRecyclerAdapter)mAdapter).getDbId();
            switch (item.getItemId()){
                case R.id.action_share_message:
                    //Check if item has image or is plain text
                    cursor= db.query(HandbookContract.NotificationEntry.TABLE_NAME,
                            null,
                            "_id= ?", new String[] {Long.toString(dbId)}, null, null, null, null);
                    if(cursor.moveToFirst()) {

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
                    status=true;
                    Log.d(TAG, "onContextItemSelected: Share item called");
                    break;
                case R.id.action_delete:

                    //Code to delete the item
                    db.delete(HandbookContract.NotificationEntry.TABLE_NAME, HandbookContract.NotificationEntry._ID + "=" + dbId,null);

                    //Update the view
                    for(int i=0; i<adapter.selectedItems.size();i++ ){
                        int key =adapter.selectedItems.keyAt(i);
                        if(adapter.selectedItems.get(key,false)){
                            mAdapter.notifyItemRemoved(key);
                            adapter.selectedItems.delete(key);
                        }
                    }



                    Toast.makeText(getContext(),"Deleted", Toast.LENGTH_SHORT);
                    Log.d(TAG, "onContextItemSelected: Delete Item Called");
                    mode.finish();
                    status=true;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        downloadManager=(DownloadManager)getContext().getSystemService(DOWNLOAD_SERVICE);
        if(receiversRegistered) {
            registerDownloadManagerIntentReceivers();
        }

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());

        db = handbookDbHelper.getReadableDatabase();


        cursor = db.rawQuery(query_to_fetch_earliest, null);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an timetableAdapter (see also next example)
        mAdapter = new MyRecyclerAdapter(this.getContext(), cursor, this);
        mRecyclerView.setAdapter(mAdapter);
        ((MyRecyclerAdapter) mAdapter).setActivity((AppCompatActivity) getActivity());
        ((MyRecyclerAdapter) mAdapter).setNotesContext(notesContext);
        return rootView;
    }

    private void registerDownloadManagerIntentReceivers() {
        getContext().registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        getContext().registerReceiver(onNotificationClick,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
        receiversRegistered=true;
    }

    private void unRegisterDownloadManagerIntentReceivers() {

        getContext().unregisterReceiver(onComplete);
        getContext().unregisterReceiver(onNotificationClick);
        receiversRegistered=false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!receiversRegistered){
            registerDownloadManagerIntentReceivers();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        unRegisterDownloadManagerIntentReceivers();
    }

    private String prepareTextMessage(String title, String detail, String from, int date) {
        return "Message " + title + " date: " + date + " from: " + from + "\\n detail: " + detail;
    }

    private void setIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){

        super.onCreateOptionsMenu(menu,menuInflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        final SearchView searchView = (SearchView)searchItem.getActionView();

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
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
                String searchQuery = "SELECT *  FROM "+ HandbookContract.NotificationEntry.TABLE_NAME + " where " + HandbookContract.NotificationEntry.COLUMN_DETAIL +" like \'%"+ query + "%\'";// order  by datetime(" + HandbookContract.NotificationEntry.COLUMN_TIMESTAMP+") DESC ";
                cursor = db.rawQuery(searchQuery, null);
                //mAdapter = new MyRecyclerAdapter(getContext(),cursor,this);
                mRecyclerView.setAdapter(mAdapter);

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
                intent.putExtra("ID", position);
                getContext().startActivity(intent);

                break;
        }


    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
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

    private void openDownloadedAttachment(final Context context, final long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
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
        if(attachmentUri!=null) {
            // Get Content Uri.
            if (ContentResolver.SCHEME_FILE.equals(attachmentUri.getScheme())) {
                // FileUri - Convert it to contentUri.
                File file = new File(attachmentUri.getPath());
                attachmentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName()+".com.myapp.handbook.provider", file);;
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

    BroadcastReceiver onNotificationClick=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Downloading message", Toast.LENGTH_LONG).show();
        }
    };

    private void handleDownloadClick(int message_id) {

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(getContext());
        String imageUrl=null;
        db = handbookDbHelper.getReadableDatabase();

        cursor= db.query(HandbookContract.NotificationEntry.TABLE_NAME,
                null,
                "_id= ?", new String[] {Long.toString(message_id)}, null, null, null, null);
        if(cursor.moveToFirst()) {
            //int id = cursor.getInt(0);
            // int notificationId = cursor.getInt(0);
            String title = cursor.getString(5);
            String detail = cursor.getString(4);
            String date = cursor.getString(3);
            String from = cursor.getString(6);
            int priority = cursor.getInt(2);
            imageUrl = cursor.getString(7);
        }

        if(imageUrl!=null && !imageUrl.isEmpty()){
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
                Snackbar.make(getView(), "Please enable storage permission",Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String [] permissions= {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};;
                                ActivityCompat.requestPermissions(getActivity(), permissions, AndroidPermissions.REQUEST_STORAGE);
                            }
                        })
                        .show();
            } else {
                // User has deny permission and checked never show permission dialog so you can redirect to Application settings page
                Snackbar.make(getView(), "Please enable permission from settings",Snackbar.LENGTH_INDEFINITE)
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
        Uri uri= Uri.parse(downloadUrl);
        String fileName =HttpConnectionUtil.getFileNameFromUrl(downloadUrl);
        Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs();

        lastDownload=
                downloadManager.enqueue(new DownloadManager.Request(uri)
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE)
                        .setTitle("SchoolLink")
                        .setDescription(fileName)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                fileName));


    }


}
