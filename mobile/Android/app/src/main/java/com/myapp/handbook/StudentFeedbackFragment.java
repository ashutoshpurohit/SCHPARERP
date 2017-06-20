package com.myapp.handbook;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.MsgType;
import com.myapp.handbook.domain.RoleProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentFeedbackFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "Network Connect";
    public static final String MESSAGE_HEADER ="MessageTitle";
    public static final String MESSAGE_BODY ="MessageBody";
    public static final String MOBILE_TO_SEND ="MobileNumbers";
    //private static final int REQUEST_PHOTO = 2;


    View fragmentView=null;
    List<String> teacherNames;
    private int selectedTeacherIndex=0;
    private String selectedStudentId="105";
    private SQLiteDatabase db;
    //List<RoleProfile> studentProfiles;
    RoleProfile selectedStudentProfile;
    private List<TeacherProfile> allTeacherProfiles = new ArrayList<>();
    TeacherProfile teacherProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);


        fragmentView= inflater.inflate(R.layout.fragment_feedback, container, false);
        //Set up the listener for the stdSpinner
        Spinner spinner = (Spinner) fragmentView.findViewById(R.id.teachers_spinner);

        final Intent captureImage = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());
        db = handbookDbHelper.getReadableDatabase();
        spinner.setOnItemSelectedListener(this);
        selectedStudentProfile= RoleProfile.getProfile(HttpConnectionUtil.getProfiles(),HttpConnectionUtil.getSelectedProfileId());
        selectedStudentId = selectedStudentProfile.getId();
        SetupView();
        new FetchProfileAsyncTask().execute();
        return fragmentView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_send:
                sendMessage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void sendMessage(){
        //Post message for notification
        EditText et=((EditText)fragmentView.findViewById(R.id.feeback_message));
        String messageBody= et.getText().toString();
        teacherProfile =allTeacherProfiles.get(selectedTeacherIndex);
        String[] mobileNo= {teacherProfile.mobileNumber};
        String [] toIds = {teacherProfile.id};
        String from = selectedStudentProfile.getFirstName()+" " + selectedStudentProfile.getLastName();
        String fromId =selectedStudentId;
        JSONObject messageObject = prepareMessage(mobileNo,toIds,from,fromId,messageBody);
        new PostTeacherMessageAsyncTask().execute(messageObject);
    }


    private class PostTeacherMessageAsyncTask extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected String doInBackground(JSONObject... params) {

            HttpConnectionUtil util = new HttpConnectionUtil();

            String url = HttpConnectionUtil.URL_ENPOINT + "/SendMessageToMultipleUser/";
            JSONObject message = params[0];
            //Upload the Image file if attached
            String result = util.downloadUrl(url, HttpConnectionUtil.RESTMethod.PUT, message);
            return result;
        }
        @Override
        protected void onPostExecute(String result){
            if(result!=null && result.length()> 0 ){
                Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                HttpConnectionUtil.launchHomePage(getContext());
            }
        }

    }

    private JSONObject prepareMessage(String[] toMobileNumbers,String[] toIds, String from, String  fromId, String message) {
        JSONArray numbers = new JSONArray();
        JSONArray idList = new JSONArray();
        JSONArray toNameList= new JSONArray();
        toNameList.put(teacherProfile.firstName);
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
        i=0;
        for(String id:toIds){
            try {
                idList.put(i,id);
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            msgToSend.put("MessageBody",message);
            msgToSend.put("type", MsgType.PARENT_NOTE);
            msgToSend.put("MessageTitle","Note from "+from);
            msgToSend.put("MobileNumbers",numbers);
            msgToSend.put("FromId",fromId);
            msgToSend.put("FroName",selectedStudentProfile.getFirstName());
            msgToSend.put("ToIds",idList );
            msgToSend.put("ToNames",toNameList);
            msgToSend.put("FromType", "Parent");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgToSend;
    }


    private void SetupView() {
        View view = fragmentView;
        TextView fromText = (TextView) view.findViewById(R.id.feedback_from);
        if (allTeacherProfiles!=null &&!allTeacherProfiles.isEmpty()) {
            //Clear the loading message
            fromText.setText("");
            //Extract teachers name from profiles array
            teacherNames= new ArrayList<>();
            //TO-DO figure out a way to differentiate between two teachers
            for(int i=0;i<allTeacherProfiles.size();i++){
                String fullName = allTeacherProfiles.get(i).firstName+ " "+allTeacherProfiles.get(i).lastName;
                teacherNames.add(fullName);
            }
            //Set up the timetableAdapter for stdSpinner
            Spinner spinner = (Spinner)fragmentView.findViewById(R.id.teachers_spinner);
            // Create an ArrayAdapter using the string array and a default stdSpinner layout
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.custom_spinner_item,teacherNames);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the timetableAdapter to the stdSpinner
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
     * @param position The position of the view in the timetableAdapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selectedTeacherIndex =position;
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the timetableAdapter becomes empty.
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

        MenuItem sendItem = menu.findItem(R.id.action_send);
        sendItem.setVisible(true);

    }






}
