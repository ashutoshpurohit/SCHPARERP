package com.myapp.handbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.MsgType;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.util.ImageCompression;
import com.myapp.handbook.util.ImageFilePath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TeacherNoteFragment extends Fragment implements View.OnClickListener {


    public static final String TAG = "Teacher Note Fragment";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    View fragmentView = null;
    SQLiteDatabase db;
    String selectedTeacherId;
    List<RoleProfile> teacherProfile;
    List<Assignment> teacherAssignments = new ArrayList<>();
    List<String> stds = new ArrayList<>();
    List<String> subjects = new ArrayList<>();
    TextView messageText;
    RadioButton homeworkButton;
    RadioButton diaryNoteButton;
    ProgressDialog progressDialog;
    MsgType msgType;
    Intent captureImage;
    boolean canTakePhoto;
    Spinner stdSpinner;
    ArrayList<RoleProfile> selectedStudents = new ArrayList<>();
    Uri photoURI;
    String userChoosenTask;
    Uri compressedPhotoURI;
    File compressedPhotoFile;
    private OnFragmentInteractionListener mListener;
    //private ImageButton photoButton;
    private ImageView photoView;
    private TextView studentCountTextView;
    private File photoFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_teacher_note, container, false);
        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());
        db = handbookDbHelper.getReadableDatabase();
        teacherProfile = RoleProfile.GetProfileForRole(db, RoleProfile.ProfileRole.TEACHER);
        selectedTeacherId = teacherProfile.get(0).getId();
        studentCountTextView = (TextView) fragmentView.findViewById(R.id.selected_student_count);
        messageText = (TextView) fragmentView.findViewById(R.id.teacher_note);
        Button selectStudent = (Button) fragmentView.findViewById(R.id.studentSelect);
        selectStudent.setOnClickListener(this);
        photoView = (ImageView) fragmentView.findViewById(R.id.message_image);

        homeworkButton = (RadioButton) fragmentView.findViewById(R.id.msg_type_homework);
        diaryNoteButton = (RadioButton) fragmentView.findViewById(R.id.msg_type_diarynote);
        //Set up the timetableAdapter for stdSpinner
        stdSpinner = (Spinner) fragmentView.findViewById(R.id.std_spinner);
        diaryNoteButton.setChecked(true);
        msgType = MsgType.DIARY_NOTE;

        homeworkButton.setOnClickListener(this);
        diaryNoteButton.setOnClickListener(this);

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

        //Check if there is a internet connection
        if (HttpConnectionUtil.isOnline(this.getActivity().getApplicationContext()) == true) {
            new FetchTeacherAssignmentAsyncTask().execute();
            SetupView();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            selectStudent.setClickable(false);
            homeworkButton.setClickable(false);
            diaryNoteButton.setClickable(false);
        }

        updatePhotoView();
        return fragmentView;
    }

    private boolean checkMessageForValidity() {
        boolean status = true;
        if (selectedStudents == null || selectedStudents.size() <= 0) {
            Toast.makeText(getContext(), "Failure Sending! No students selected. Please select student before sending", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (messageText.getText().length() <= 0) {

            Toast.makeText(getContext(), "Failure Sending! Message is empty. Please input some message.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    public void SetupView() {
        View view = fragmentView;
        TextView fromText = (TextView) view.findViewById(R.id.feedback_from);
        if (teacherAssignments != null && !teacherAssignments.isEmpty()) {
            fromText.setText("");
            stds.clear();
            subjects.clear();
            //TO-DO figure out a way to differentiate between two teachers
            for (int i = 0; i < teacherAssignments.size(); i++) {
                String std = teacherAssignments.get(i).std;
                String subject = teacherAssignments.get(i).subject;
                if (!stds.contains(std))
                    stds.add(std);
                if (!subjects.contains(subject))
                    subjects.add(subject);
            }

            // Create an ArrayAdapter using the string array and a default stdSpinner layout
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, stds);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the timetableAdapter to the stdSpinner
            stdSpinner.setAdapter(adapter);

            /*Spinner subjectSpinner = (Spinner)fragmentView.findViewById(R.id.subject_spinner);
            // Create an ArrayAdapter using the string array and a default stdSpinner layout
            ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,subjects);
            // Specify the layout to use when the list of choices appears
            subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the timetableAdapter to the stdSpinner
            subjectSpinner.setAdapter(subjectAdapter);
*/
        } else {

            fromText.setText("Loading details. Please wait..");
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.studentSelect:
                Intent intent = new Intent(getActivity(), StudentSearch.class);
                String selectedClass = (String) stdSpinner.getSelectedItem();
                if (HttpConnectionUtil.isOnline(this.getActivity().getApplicationContext())) {
                    intent.putExtra("Teacher_ID", selectedTeacherId);
                    intent.putExtra("Std", selectedClass);
                    intent.putParcelableArrayListExtra("lastSelectedStudents", selectedStudents);
                    startActivityForResult(intent, 111);
                }
                break;
            case R.id.msg_type_homework:
                homeworkButton.setChecked(true);
                msgType = MsgType.HOMEWORK;
                break;
            case R.id.msg_type_diarynote:
                diaryNoteButton.setChecked(true);
                msgType = MsgType.DIARY_NOTE;
                break;
            default:
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {

        super.onActivityResult(requestCode, resultCode, result);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(result);
            else if (requestCode == REQUEST_CAMERA) {
                updatePhotoView();
            } else if (result != null) {
                selectedStudents = result.getParcelableArrayListExtra("selectedStudent");
                if (selectedStudents != null) {
                    int count = selectedStudents.size();
                    studentCountTextView.setText("Selected: " + count);
                }
            }
        }
    }

    private JSONObject prepareMessage() {
        JSONArray numbers = new JSONArray();
        JSONArray ids = new JSONArray();
        JSONArray studentNames = new JSONArray();
        JSONObject msgToSend = new JSONObject();
        int i = 0;
        for (RoleProfile student : selectedStudents
                ) {
            try {
                numbers.put(i, student.getMobileNumber());
                ids.put(student.getId());
                studentNames.put(student.getFirstName());
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        String message = "";
        View view = fragmentView;

        message = messageText.getText().toString();
        try {
            msgToSend.put("MessageBody", message);
            msgToSend.put("MessageTitle", "Teacher Note from " + teacherProfile.get(0).getFirstName());
            msgToSend.put("MobileNumbers", numbers);
            msgToSend.put("type", msgType.toString());
            msgToSend.put("ToIds", ids);
            msgToSend.put("FromType", "Teacher");
            msgToSend.put("FromId", selectedTeacherId);
            msgToSend.put("FromName", teacherProfile.get(0).getFirstName());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgToSend;
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(captureImage, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);


        /*Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image*//*");
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);*/
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null && data.getData() != null) {

            Uri uri = data.getData();

            String realPath = ImageFilePath.getPath(getActivity(), data.getData());
//                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
            //compressedPhotoFile = new File(uri.toString());
            ImageCompression imgCompression = new ImageCompression(getContext());
            File destDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            compressedPhotoFile = new File(imgCompression.compress(realPath, destDir, false));

            Log.i(TAG, "onActivityResult: file path : " + realPath);
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));


            } catch (IOException e) {
                e.printStackTrace();
            }

            //bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            /*} catch (IOException e) {
                e.printStackTrace();
            }
        }*/

            photoView.setImageBitmap(bm);
            photoView.setVisibility(View.VISIBLE);

        }
    }


    public void updatePhotoView() {
        if (photoFile == null || !photoFile.exists()) {
            photoView.setImageDrawable(null);
            photoView.setVisibility(View.GONE);
        } else {
            ImageCompression imgCompression = new ImageCompression(getContext());
            File destDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            compressedPhotoFile = new File(imgCompression.compress(photoFile.getPath(), destDir, true));
            Bitmap bitmap = PictureUtil.getScaledBitmap(compressedPhotoFile.getPath(), getActivity());
            photoView.setImageBitmap(bitmap);
            photoView.setVisibility(View.VISIBLE);
        }
    }


    private void selectImageDialog() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_add_picture:
                //startActivityForResult(captureImage,REQUEST_PHOTO);
                selectImageDialog();
                break;
            case R.id.action_send:
                if (HttpConnectionUtil.isOnline(this.getActivity().getApplicationContext())) {
                    boolean canSend = checkMessageForValidity();
                    if (canSend) {
                        progressDialog = ProgressDialog.show(getContext(), "Sending message", "Please wait", false);
                        new PostTeacherMessageAsyncTask().execute();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        //Hide all menu icon
        for (int i = 0; i < menu.size()-1; i++)
            menu.getItem(i).setVisible(false);

        MenuItem sendItem = menu.findItem(R.id.action_send);
        MenuItem addPicture = menu.findItem(R.id.action_add_picture);
        sendItem.setVisible(true);
        /*sendItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });*/
        //sendItem.setOnMenuItemClickListener(this);
        addPicture.setVisible(true);
        addPicture.setEnabled(canTakePhoto);
        /*addPicture.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivityForResult(captureImage,REQUEST_PHOTO);
                return false;
            }
        });*/
        //addPicture.setOnMenuItemClickListener(this);
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
        void onFragmentInteraction(Uri uri);
    }

    private class Assignment {
        public String std;
        public String subject;
    }

    private class FetchTeacherAssignmentAsyncTask extends AsyncTask<Void, Void, List<Assignment>> {
        @Override
        protected List<Assignment> doInBackground(Void... params) {
            HttpConnectionUtil util = new HttpConnectionUtil();
            String mobileNumber = HttpConnectionUtil.getMobileNumber();
            List<Assignment> teacherAssignment = new ArrayList<>();
            String url = HttpConnectionUtil.URL_ENPOINT + "/GetTeacherOrParentRole/" + mobileNumber;
            String result = util.downloadUrl(url, HttpConnectionUtil.RESTMethod.GET, null);
            Log.i(TAG, "Received JSON for teacher's list:" + result);
            try {
                JSONObject objectList = new JSONObject(result);
                JSONArray teacherRoles = objectList.getJSONObject("Teacher").getJSONArray("TeacherRoleList");
                for (int i = 0; i < teacherRoles.length(); i++) {

                    Assignment assgn = new Assignment();
                    JSONObject role = (JSONObject) teacherRoles.get(i);
                    assgn.std = role.getString("TeacherRoleforStd");
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
            teacherAssignments = curteacherAssignment;
            SetupView();
        }
    }

    private class PostTeacherMessageAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            HttpConnectionUtil util = new HttpConnectionUtil();

            String url = HttpConnectionUtil.URL_ENPOINT + "/SendMessageToMultipleUser/";
            JSONObject messageJson = prepareMessage();
            if(compressedPhotoFile!=null && compressedPhotoFile.exists()) {
                HttpConnectionUtil.UploadImage(compressedPhotoFile);
                while (!HttpConnectionUtil.imageUploaded) {

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

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result != null && result.length() > 0) {
                Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                //Go to home page
                HttpConnectionUtil.launchHomePage(getContext());
            }

        }

    }

}
