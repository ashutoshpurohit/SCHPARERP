package com.myapp.handbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.myapp.handbook.Listeners.ProfileImageClickListener;
import com.myapp.handbook.Listeners.SelectionChangeListener;
import com.myapp.handbook.Tasks.FetchProfileAsyncTask;
import com.myapp.handbook.Tasks.UpdateNavigationViewHeader;
import com.myapp.handbook.adapter.ProfileAdapter;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.SchoolProfile;
import com.myapp.handbook.util.ImageCompression;
import com.myapp.handbook.util.ImageFilePath;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.internal.zzahf.runOnUiThread;
import static com.myapp.handbook.domain.RoleProfile.saveProfilestoDB;

public class TopFragment extends Fragment {

    private static final String TAG = "ProfileEntry Fetch";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    View header;
    SharedPreferences sharedPreferences;
    ListView listView;
    Intent captureImage;
    boolean canTakePhoto;
    Uri photoURI;
    String userChoosenTask;
    Uri compressedPhotoURI;
    File compressedPhotoFile;
    //ImageView photoView;
    Bitmap bitmap;
    ProfileAdapter adapter;
    private List<RoleProfile> allProfiles = new ArrayList<>();
    private SchoolProfile schoolProfile = null;
    private NavigationView navigationView=null;
    private View fragmentView;
    private SQLiteDatabase db;
    private Cursor cursor;
    private File photoFile;
    private String profileImageURL;
    private String selectedProfileId;

    public void setNavigationView(NavigationView navigationView) {
        this.navigationView = navigationView;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        fragmentView = view;

        //header = inflater.inflate(R.layout.listview_profile_header, null);
        listView= (ListView) view.findViewById(R.id.profileListView1);
        //listView.addHeaderView(header);

        //setHasOptionsMenu(true);

        setImageSelectionView();

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());

        db = handbookDbHelper.getReadableDatabase();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        List<FetchProfileAsyncTask.ProfileDownloadListener> listeners = new ArrayList<>();
        listeners.add(new FetchProfileAsyncTask.ProfileDownloadListener() {
            @Override
            public void onProfileDownload(List<RoleProfile> profiles, SchoolProfile schoolProfile) {

                saveProfilestoDB(profiles,db,sharedPreferences);
            }
        });
        listeners.add(new FetchProfileAsyncTask.ProfileDownloadListener() {
            @Override
            public void onProfileDownload(List<RoleProfile> profiles, SchoolProfile schoolProfile) {

                if(profiles!=null && profiles.size() >0 ) {
                    HttpConnectionUtil.setSelectedProfileId(profiles.get(0).getId());
                    HttpConnectionUtil.setProfiles(profiles);
                }
            }
        });

        listeners.add(new FetchProfileAsyncTask.ProfileDownloadListener(){

            @Override
            public void onProfileDownload(List<RoleProfile> profiles, SchoolProfile schoolProfile) {

                SetUpView(profiles,fragmentView);
            }
        });
        //sharedPreferences.edit().putBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, false).apply();
        if (!sharedPreferences.getBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, false)) {
            //Download the profile
            new FetchProfileAsyncTask(listeners,getContext()).execute();

        } else
        {
            allProfiles=HandBookDbHelper.LoadProfilefromDb(db);
            SetUpView(allProfiles,fragmentView);
        }

        return view;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem item=menu.findItem(R.id.action_search);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    public void SetUpView(List<RoleProfile> allProfiles, View fragmentView) {

        //TextView headerText = (TextView) header.findViewById(R.id.profileHeader);
        if (!allProfiles.isEmpty()) {
            RoleProfile [] profiles =new RoleProfile[allProfiles.size()];
            profiles=   allProfiles.toArray(profiles);
            List<SelectionChangeListener> selectionChangedListeners = new ArrayList<>();
            selectionChangedListeners.add(new UpdateNavigationViewHeader(allProfiles,navigationView,getContext()));
            List<ProfileImageClickListener> imageClickListeners = new ArrayList<>();
            adapter = new ProfileAdapter(getContext(), R.layout.list_item_profile, profiles, selectionChangedListeners, imageClickListeners);
            imageClickListeners.add(new ProfileImageClickListener() {
                @Override
                public void onProfileImageClicked(String currentProfileId) {

                    selectImageDialog(currentProfileId);

                }
            });
            //adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
            //headerText.setText("Profile");
        } else {
            //firstName.setText("Loading the profile info. Please wait..");

            //headerText.setText("Loading ..");
        }

    }


    private void selectImageDialog(final String currentProfileId) {
        selectedProfileId = currentProfileId;
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = PictureUtil.checkPermission(getContext());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PictureUtil.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void cameraIntent() {

        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        this.startActivityForResult(captureImage, REQUEST_CAMERA);
    }

    private void galleryIntent() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//

        this.startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);


    }

    public void setImageSelectionView() {
        photoFile = HttpConnectionUtil.getPhotoFile(getContext(), HttpConnectionUtil.getPhotoFileName());

        captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Check if permission is available to create file and access camera
        canTakePhoto = photoFile != null && captureImage.resolveActivity(getContext().getPackageManager()) != null;

        Context context = getContext();
        if (canTakePhoto) {
            //Uri uri = Uri.fromFile( photoFile);
            photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".com.myapp.handbook.provider", photoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            captureImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent result) {

        super.onActivityResult(requestCode, resultCode, result);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(result);
            else if (requestCode == REQUEST_CAMERA) {
                updatePhotoView(result);
            }
        }
    }

    public void updatePhotoView(Intent result) {

        if (photoFile == null || !photoFile.exists()) {
            //photoView.setImageDrawable(null);
            //photoView.setVisibility(View.GONE);
        } else {
            ImageCompression imgCompression = new ImageCompression(getContext());
            File destDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            compressedPhotoFile = new File(imgCompression.compress(photoFile.getPath(), destDir, true));
            // bitmap = PictureUtil.getScaledBitmap(compressedPhotoFile.getPath(), getActivity());
            new UploadImageAsyncTask().execute();
            /*photoView.setImageBitmap(bitmap);
            photoView.setVisibility(View.VISIBLE);*/
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {


        Bitmap bm = null;
        if (data != null && data.getData() != null) {

            Uri uri = data.getData();

            String realPath = ImageFilePath.getPath(getActivity(), data.getData());
//
            ImageCompression imgCompression = new ImageCompression(getContext());
            File destDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            compressedPhotoFile = new File(imgCompression.compress(realPath, destDir, false));

            Log.i(TAG, "onActivityResult: file path : " + realPath);
            /*try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));


            } catch (IOException e) {
                e.printStackTrace();
            }*/

            if (HttpConnectionUtil.isOnline(this.getActivity().getApplicationContext()) == true) {
                new UploadImageAsyncTask().execute();

            }
        }
    }

    //function to get profile type
    private RoleProfile.ProfileRole getProfileType(String profileId) {
        RoleProfile.ProfileRole profileType = null;

        for (int j = 0; j < allProfiles.size(); j++) {
            if (allProfiles.get(j).getId() == profileId) {
                profileType = allProfiles.get(j).getProfileRole();
            }
        }


        return profileType;
    }

    //function to get url based on profile type
    private String getUploadedImageUrl(String profileId) {
        RoleProfile.ProfileRole profileRole;
        String url = null;
        profileRole = getProfileType(profileId);
        if (profileRole == RoleProfile.ProfileRole.STUDENT) {
            url = HttpConnectionUtil.URL_ENPOINT + "/students/" + selectedProfileId + "/" + "Image";
        } else if (profileRole == RoleProfile.ProfileRole.TEACHER) {
            url = HttpConnectionUtil.URL_ENPOINT + "/teachers/" + selectedProfileId + "/" + "Image";
        }
        return url;
    }


    private String uploadImage() {
        RoleProfile.ProfileRole profileRole;
        String url = null;
        HttpConnectionUtil util = new HttpConnectionUtil();

        url = getUploadedImageUrl(selectedProfileId);
        JSONObject messageJson = new JSONObject();
        if (compressedPhotoFile != null && compressedPhotoFile.exists()) {

            //ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Setting profile image", "Please wait", false);
            HttpConnectionUtil.UploadImage(compressedPhotoFile);


            while (!HttpConnectionUtil.imageUploaded) {
                //Waiting for upload to complete
            }

            //progressDialog.dismiss();
            if (HttpConnectionUtil.imageUploadStatus) {
                profileImageURL = HttpConnectionUtil.imageUrl;

                HandBookDbHelper.updateProfile(db, profileImageURL, selectedProfileId);

            } else {
                // Toast.makeText(getContext(), "Selected Image Cannot be loaded,please select another image!", Toast.LENGTH_LONG).show();
                showToast("Selected Image Cannot be loaded,please select another image!");
            }
            try {
                if (HttpConnectionUtil.imageUploadStatus) {
                    messageJson.put("ImageUrl", HttpConnectionUtil.imageUrl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return util.downloadUrl(url, HttpConnectionUtil.RESTMethod.PUT, messageJson);


    }


    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getContext(), toast, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        //Hide search menu icon
        //menu.getItem(0).setVisible(false);
    }

    private class UploadImageAsyncTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Updating profile image, Please wait..");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            uploadImage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            allProfiles = HandBookDbHelper.LoadProfilefromDb(db);
            RoleProfile[] profiles = new RoleProfile[allProfiles.size()];
            profiles = allProfiles.toArray(profiles);
            adapter.setRoles(profiles);
            adapter.notifyDataSetChanged();

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }


}
