package com.myapp.handbook;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.myapp.handbook.adapter.MultiSelectionAdapter;
import com.myapp.handbook.profile.RoleProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentSearch extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "StudentSearch";
    String teacherId;
    ArrayList<RoleProfile> allStudentsProfile= new ArrayList<>();
    ArrayList<RoleProfile> selectedStudents = new ArrayList<>();
    MultiSelectionAdapter<RoleProfile> multiSelectAdapter;
    ListView studentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);

        studentList = (ListView)findViewById(R.id.studentList);
        Button ok =(Button) findViewById(R.id.doneButton);
        ok.setOnClickListener(this);
                //Get the teacher Id
        Intent intent = getIntent();
        teacherId = intent.getStringExtra("Teacher_ID");
        new FetchStudentProfileAsyncTask().execute();
        SetupView();

    }

    private void SetupView() {

        multiSelectAdapter = new MultiSelectionAdapter<RoleProfile>(this,allStudentsProfile);
        studentList.setAdapter(multiSelectAdapter);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        selectedStudents = multiSelectAdapter.getCheckedItems();
        Intent result = new Intent();
        result.putParcelableArrayListExtra("selectedStudent",selectedStudents);
        setResult(RESULT_OK,result);
        finish();
    }


    private class FetchStudentProfileAsyncTask extends AsyncTask<Void, Void, ArrayList<RoleProfile>> {
        @Override
        protected ArrayList<RoleProfile> doInBackground(Void... params) {
            HttpConnectionUtil util = new HttpConnectionUtil();
            ArrayList<RoleProfile> profiles=null;

            String url =HttpConnectionUtil.URL_ENPOINT + "/GetAllStudentDetailsForTeacher/"+ teacherId;
            String result = util.downloadUrl(url, HttpConnectionUtil.RESTMethod.GET, null);
            Log.i(TAG, "Received JSON :" + result);
            try {
                profiles = new ArrayList<>();
                JSONObject jsonBody = new JSONObject(result);
                JSONArray students = null;
                if (jsonBody.has("StudentList"))
                    students = jsonBody.getJSONArray("StudentList");

                for (int i = 0; i < students.length(); i++) {
                    RoleProfile profile = RoleProfile.parsePartialStudentJSonObject(students.getJSONObject(i));
                    if (profile != null)
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
        protected void onPostExecute(ArrayList<RoleProfile> profiles) {
            allStudentsProfile = profiles;
            SetupView();
        }
    }

}
