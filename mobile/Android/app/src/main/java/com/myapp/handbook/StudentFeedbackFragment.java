package com.myapp.handbook;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.profile.RoleProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentFeedbackFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static final String TAG = "Network Connect";
    public static final String MESSAGE_HEADER ="MessageTitle";
    public static final String MESSAGE_BODY ="MessageBody";
    public static final String MOBILE_TO_SEND ="MobileNumbers";
    private static final int REQUEST_PHOTO = 2;


    View fragmentView=null;
    List<String> teacherNames;
    private int selectedTeacherIndex=0;
    private String selectedStudentId="105";
    private SQLiteDatabase db;
    List<String> studentIds;
    private List<TeacherProfile> allTeacherProfiles = new ArrayList<>();
    private ImageButton photoButton;
    private ImageView photoView;
    private File photoFile;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);


        fragmentView= inflater.inflate(R.layout.fragment_feedback, container, false);
        //Set up the listener for the spinner
        Spinner spinner = (Spinner) fragmentView.findViewById(R.id.teachers_spinner);
        photoButton =(ImageButton)fragmentView.findViewById(R.id.camera_button);
        photoView =(ImageView)fragmentView.findViewById(R.id.message_image);
        photoFile = HttpConnectionUtil.getPhotoFile(getContext(),HttpConnectionUtil.getPhotoFileName());

        final Intent captureImage = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);

        //Check if permission is available to create file and access camera
        boolean canTakePhoto = photoFile!=null && captureImage.resolveActivity(getContext().getPackageManager())!=null;
        photoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile( photoFile);
            captureImage.putExtra( MediaStore.EXTRA_OUTPUT, uri);
        }

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });



        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());
        db = handbookDbHelper.getReadableDatabase();
        spinner.setOnItemSelectedListener(this);
        Button sendButton = (Button)fragmentView.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
        studentIds= RoleProfile.GetIdsForRole(db, RoleProfile.ProfileRole.STUDENT);
        if(studentIds.size()==1)
        {
            selectedStudentId = studentIds.get(0);
        }
        else{
            //TO-DO Insert logic to ask user to select one student
            selectedStudentId = studentIds.get(0);
        }
        SetupView();
        new FetchProfileAsyncTask().execute();
        updatePhotoView();
        return fragmentView;
    }

    public void updatePhotoView(){
        if(photoFile==null|| photoFile.exists()==false ){
            photoView.setImageDrawable(null);
            photoView.setVisibility(View.GONE);
        }
        else{
            Bitmap bitmap = PictureUtil.getScaledBitmap(photoFile.getPath(),getActivity());
            photoView.setImageBitmap(bitmap);
            photoView.setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View v){
        //Post message for notification
        EditText et=((EditText)fragmentView.findViewById(R.id.feeback_message));
        String messageBody= et.getText().toString();
        String[] mobileNo= {allTeacherProfiles.get(selectedTeacherIndex).mobileNumber};

        //To-Do hardcoded header to be changed
        //To-Do  Figure out from which profile message is sent
        String messageHeader ="Note from Parent";
        JSONObject messageObject = prepareMessage(mobileNo,"Parent",messageBody);
        progressDialog= ProgressDialog.show(getContext(),"Sending message","Please wait",false);
        new PostTeacherMessageAsyncTask().execute(messageObject);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result){
        if(requestCode==REQUEST_PHOTO)
            updatePhotoView();

    }

    private class PostTeacherMessageAsyncTask extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected String doInBackground(JSONObject... params) {

            HttpConnectionUtil util = new HttpConnectionUtil();

            String url = HttpConnectionUtil.URL_ENPOINT + "/SendMessageToMultipleUser/";
            JSONObject message = params[0];
            //Upload the Image file if attached
            util.UploadImage(photoFile);
            while (HttpConnectionUtil.imageUploaded==false){

            }
            try {
                if(HttpConnectionUtil.imageUploadStatus) {
                    message.put("ImageName", HttpConnectionUtil.imageUrl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String result = util.downloadUrl(url, HttpConnectionUtil.RESTMethod.PUT, message);
            return result;
        }
        @Override
        protected void onPostExecute(String result){
            if(result!=null && result.length()> 0 ){
                Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }

    }




    private JSONObject prepareMessage(String[] toMobileNumbers, String from, String message) {
        JSONArray numbers = new JSONArray();
        JSONObject msgToSend = new JSONObject();
        int i=0;
        for (String number:toMobileNumbers
                ) {
            try {
                numbers.put(i,number);
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            msgToSend.put("MessageBody",message);
            msgToSend.put("MessageTitle","Teacher Note from "+from);
            msgToSend.put("MobileNumbers",numbers);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgToSend;
    }


    private void SetupView() {
        View view = fragmentView;
        TextView fromText = (TextView) view.findViewById(R.id.feedback_from);
        if (!allTeacherProfiles.isEmpty()) {
            //Clear the loading message
            fromText.setText("");
            //Extract teachers name from profiles array
            teacherNames= new ArrayList<>();
            //TO-DO figure out a way to differentiate between two teachers
            for(int i=0;i<allTeacherProfiles.size();i++){
                String fullName = allTeacherProfiles.get(i).firstName+ " "+allTeacherProfiles.get(i).lastName;
                teacherNames.add(fullName);
            }
            //Set up the adapter for spinner
            Spinner spinner = (Spinner)fragmentView.findViewById(R.id.teachers_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,teacherNames);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);


        } else {
            fromText.setText("Loading details. Please wait..");
        }
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selectedTeacherIndex =position;
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class FetchProfileAsyncTask extends AsyncTask<Void, Void, List<TeacherProfile>> {
        @Override
        protected List<TeacherProfile> doInBackground(Void... params) {
            HttpConnectionUtil util = new HttpConnectionUtil();
            List<TeacherProfile> profiles=null;
            String url = HttpConnectionUtil.URL_ENPOINT + "/TeacherDetailForStudent/"+ selectedStudentId;
            String result = util.downloadUrl(url, HttpConnectionUtil.RESTMethod.GET, null);
            Log.i(TAG, "Received JSON for teacher's list:" + result);
            try {
                profiles = new ArrayList<>();

                JSONObject objectList = new JSONObject(result);
                JSONArray teachers = objectList.getJSONArray("Teachers");
                for(int i=0;i<teachers.length();i++){
                    TeacherProfile profile;
                    profile = TeacherProfile.parseTeacherObject((JSONObject) teachers.get(i));
                    if(profile!=null)
                        profiles.add(profile);
                }
                return profiles;
            } catch (JSONException e) {
                Log.i(TAG, "Failed to parse JSON :" + result);
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(List<TeacherProfile> profiles) {
            allTeacherProfiles = profiles;
            SetupView();
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        //Hide all menu icon
        for (int i = 0; i < menu.size()-1; i++)
            menu.getItem(i).setVisible(false);
    }






}
