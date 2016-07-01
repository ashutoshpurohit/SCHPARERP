package com.myapp.handbook;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.data.HandbookContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StudentFeedbackFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static final String TAG = "Network Connect";
    public static final String MESSAGE_HEADER ="MessageTitle";
    public static final String MESSAGE_BODY ="MessageBody";
    public static final String MOBILE_TO_SEND ="MobileNumbers";

    View fragmentView=null;
    List<String> teacherNames;
    private int selectedTeacherIndex=0;
    private String selectedStudentId="105";
    private SQLiteDatabase db;
    List<String> studentIds;
    private List<TeacherProfile> allTeacherProfiles = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView= inflater.inflate(R.layout.fragment_feedback, container, false);
        //Set up the listener for the spinner
        Spinner spinner = (Spinner) fragmentView.findViewById(R.id.teachers_spinner);

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());
        db = handbookDbHelper.getReadableDatabase();
        spinner.setOnItemSelectedListener(this);
        Button sendButton = (Button)fragmentView.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
        studentIds= GetStudentIds();
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
        return fragmentView;
    }

    private List<String> GetStudentIds() {

        List<String> studentIds= new ArrayList<>();

        List<Profile> allProfiles = HandBookDbHelper.LoadProfilefromDb(db);
        for(Profile profile:allProfiles){
            if(profile.getRole().equalsIgnoreCase(Profile.ProfileRole.STUDENT.toString())){
                studentIds.add(profile.getId());
            }
        }
        return  studentIds;
    }

    public void onClick(View v){
        //Post message for notification
        EditText et=((EditText)fragmentView.findViewById(R.id.feeback_message));
        String messageBody= et.getText().toString();
        String mobileNo= allTeacherProfiles.get(selectedTeacherIndex).mobileNumber;
        //To-Do hardcoded header to be changed
        //To-Do  Create a separate method for preparing JSon message
        String messageHeader ="Hello";
        JSONObject messageObject = new JSONObject();
        JSONArray mobileNumbers = new JSONArray();

        try {
            messageObject.put(MESSAGE_HEADER,messageHeader);
            messageObject.put(MESSAGE_BODY,messageBody);
            mobileNumbers.put(mobileNo);
            messageObject.put(MOBILE_TO_SEND, mobileNumbers);

        } catch(JSONException e) {
            e.printStackTrace();
        }

        HttpConnectionUtil connUtil = new HttpConnectionUtil();
        String result= connUtil.downloadUrl(HttpConnectionUtil.URL_ENPOINT +"/SendMessageToMultipleUser", HttpConnectionUtil.RESTMethod.PUT,messageObject);
        Log.d(TAG,"Received result from put message "+ result);


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





}
