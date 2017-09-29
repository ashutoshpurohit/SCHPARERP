package com.myapp.handbook;

import android.app.Activity;
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
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.data.HandbookContract;
import com.myapp.handbook.util.AndroidPermissions;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotesDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotesDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesDetailFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private  long message_id;
    private SQLiteDatabase db;
    private Cursor cursor;
    DownloadManager downloadManager;
    boolean receiversRegistered=false;
    String imageUrl;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NotesDetailFragment() {
        // Required empty public constructor
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        message_id = intent.getIntExtra("ID", 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes_detail,container,false);
        downloadManager=(DownloadManager)getContext().getSystemService(DOWNLOAD_SERVICE);
        if(receiversRegistered) {
            registerDownloadManagerIntentReceivers();
        }
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
            imageUrl = cursor.getString(7);
            TextView titleTextView = (TextView)view.findViewById(R.id.detail_header);
            TextView detailTextView = (TextView)view.findViewById(R.id.detail_message);
            //TextView priorityTextView = (TextView)view.findViewById(R.id.detail_priority);
            TextView dateTextView = (TextView)view.findViewById(R.id.detail_date);
            TextView fromTextView = (TextView)view.findViewById(R.id.detail_from);
            ImageView imageDetailView = (ImageView) view.findViewById(R.id.detail_image);
            ImageView attachmentIcon = (ImageView)view.findViewById(R.id.item_msg_type_icon);
            View attachmentView = (View)view.findViewById(R.id.detail_section_file_download);
            TextView downloadFileNameView = (TextView)view.findViewById(R.id.item_file_name);
            attachmentIcon.setOnClickListener(this);
            downloadFileNameView.setOnClickListener(this);
            titleTextView.setText(title);
            detailTextView.setText(detail);
            //priorityTextView.setText(Integer.toString(priority));
            dateTextView.setText(date);
            fromTextView.setText(from);

            if(imageUrl!=null && !imageUrl.isEmpty()){
                if(HttpConnectionUtil.isImage(imageUrl)) {
                    imageDetailView.setVisibility(View.VISIBLE);
                    attachmentView.setVisibility(View.GONE);
                    Glide.with(getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.contact_picture_placeholder)
                            .error(R.drawable.contact_picture_error)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageDetailView);
                }
                else {
                    imageDetailView.setVisibility(View.INVISIBLE);
                    attachmentView.setVisibility(View.VISIBLE);
                    downloadFileNameView.setText(HttpConnectionUtil.getFileNameFromUrl(imageUrl));
                    //Register download manager if there is a file to download


                }



            }


        }

//        View v=  inflater.inflate(R.layout.fragment_notes_detail, container, false);

        return view;
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

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.item_msg_type_icon:
            case R.id.item_file_name:
                if(imageUrl!=null && !imageUrl.isEmpty()){
                    checkExternalStoragePermissionsAndDownload(imageUrl);
                }
                break;

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

    public void startDownload(String downloadUrl) {
        Uri uri= Uri.parse(downloadUrl);
        long lastDownload=-1L;
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
        void onFragmentInteraction(Uri uri);
    }

}
