package com.myapp.handbook;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.RoleProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TeacherNoteFragment extends Fragment implements View.OnClickListener {

    private class Assignment{
        public String std;
        public String subject;
    }

    private OnFragmentInteractionListener mListener;
    public static final String TAG = "Teacher Note Fragment";
    View fragmentView =null;
    SQLiteDatabase db;
    String selectedTeacherId;
    List<RoleProfile> teacherProfile;
    List<Assignment> teacherAssignments = new ArrayList<>();
    List<String> stds = new ArrayList<>();
    List<String> subjects =new ArrayList<>();
    ArrayList<RoleProfile> selectedStudents= new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        fragmentView= inflater.inflate(R.layout.fragment_teacher_note, container, false);
        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());
        db = handbookDbHelper.getReadableDatabase();
        teacherProfile = RoleProfile.GetProfileForRole(db, RoleProfile.ProfileRole.TEACHER);
        selectedTeacherId= teacherProfile.get(0).getId();

        Button selectStudent = (Button)fragmentView.findViewById(R.id.studentSelect);
        selectStudent.setOnClickListener(this);

        Button clickButton = (Button) fragmentView.findViewById(R.id.sendButton);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new PostTeacherMessageAsyncTask().execute();
            }
        });

        new FetchTeacherAssignmentAsyncTask().execute();
        SetupView();


        return fragmentView;
    }

    public void SetupView() {
        View view = fragmentView;
        TextView fromText = (TextView) view.findViewById(R.id.feedback_from);
        if (!teacherAssignments.isEmpty()) {
            fromText.setText("");
            stds.clear();
            subjects.clear();
            //TO-DO figure out a way to differentiate between two teachers
            for(int i=0;i<teacherAssignments.size();i++){
                String std = teacherAssignments.get(i).std;
                String subject = teacherAssignments.get(i).subject;
                if(!stds.contains(std))
                    stds.add(std);
                if(!subjects.contains(subject))
                    subjects.add(subject);
            }
            //Set up the adapter for spinner
            Spinner spinner = (Spinner)fragmentView.findViewById(R.id.std_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,stds);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);

            Spinner subjectSpinner = (Spinner)fragmentView.findViewById(R.id.subject_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,subjects);
            // Specify the layout to use when the list of choices appears
            subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            subjectSpinner.setAdapter(subjectAdapter);

        }
        else {
            fromText.setText("Loading details. Please wait..");
        }
    }

    private class FetchTeacherAssignmentAsyncTask extends AsyncTask<Void, Void, List<Assignment>> {
        @Override
        protected List<Assignment> doInBackground(Void... params) {
            HttpConnectionUtil util = new HttpConnectionUtil();
            String mobileNumber=HttpConnectionUtil.getMobileNumber();
            List<Assignment> teacherAssignment = new ArrayList<>();
            String url = HttpConnectionUtil.URL_ENPOINT + "/GetTeacherOrParentRole/"+ mobileNumber;
            String result = util.downloadUrl(url, HttpConnectionUtil.RESTMethod.GET, null);
            Log.i(TAG, "Received JSON for teacher's list:" + result);
            try {
                JSONObject objectList = new JSONObject(result);
                JSONArray teacherRoles = objectList.getJSONObject("Teacher").getJSONArray("TeacherRoleList");
                for(int i=0;i<teacherRoles.length();i++){

                    Assignment assgn = new Assignment();
                    JSONObject role = (JSONObject) teacherRoles.get(i);
                    assgn.std= role.getString("TeacherRoleforStd");
                    assgn.subject = role.getString("TeacherRoleforSubject");
                    teacherAssignment.add(assgn);


                }
                return teacherAssignment;
            } catch (JSONException e) {
                Log.i(TAG, "Failed to parse JSON :" + result);
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(List<Assignment> curteacherAssignment) {
           teacherAssignments= curteacherAssignment;
           SetupView();
        }
    }



    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        Intent intent = new Intent(getActivity(),StudentSearch.class);
        intent.putExtra("Teacher_ID",selectedTeacherId);
        startActivityForResult(intent,111);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result){

        if(result!=null)
            selectedStudents = result.getParcelableArrayListExtra("selectedStudent");


    }

    private class PostTeacherMessageAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            HttpConnectionUtil util = new HttpConnectionUtil();

            String url = HttpConnectionUtil.URL_ENPOINT + "/SendMessageToMultipleUser/";
            JSONObject messageJson = prepareMessage();
            String result = util.downloadUrl(url, HttpConnectionUtil.RESTMethod.PUT, messageJson);
            return result;

        }
        @Override
        protected void onPostExecute(String result){
            if(result!=null && result.length()> 0 ){
                Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private JSONObject prepareMessage() {
        JSONArray numbers = new JSONArray();
        JSONObject msgToSend = new JSONObject();
        int i=0;
        for (RoleProfile student:selectedStudents
             ) {
            try {
                numbers.put(i,student.getMobileNumber());
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        String message ="";
        View view = fragmentView;
        TextView fromText = (TextView) view.findViewById(R.id.teacher_note);
        message = fromText.getText().toString();
        try {
            msgToSend.put("MessageBody",message);
            msgToSend.put("MessageTitle","Teacher Note from "+ teacherProfile.get(0).getFirstName()+ " "+ teacherProfile.get(0).getLastName());
            msgToSend.put("MobileNumbers",numbers);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgToSend;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        //Hide all menu icon
        for (int i = 0; i < menu.size()-1; i++)
            menu.getItem(i).setVisible(false);
    }


}
