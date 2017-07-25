package com.myapp.handbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.myapp.handbook.data.HandbookContract;
import com.myapp.handbook.domain.Event;
import com.myapp.handbook.domain.MsgType;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.SchoolProfile;
import com.myapp.handbook.domain.TeacherTimeTable;
import com.myapp.handbook.domain.TimeTable;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by SAshutosh on 6/7/2016.
 */
public class HttpConnectionUtil {

    private static final String TAG = "HttConnectionUtil";
    public static final int HOMEWORK_TYPE = 1;
    public static final int DIARY_NOTE_TYPE = 2;
    public static final int PARENT_NOTE_TYPE = 3;
    public  static final int OTHER_NOTE_TYPE=0;

    public static boolean imageUploaded =false;
    public static boolean imageUploadStatus =false;
    public static String imageUrl="";
    static List<RoleProfile> profiles;

    public static List<RoleProfile> getProfiles() {
        return profiles;
    }

    public static void setProfiles(List<RoleProfile> profiles) {
        HttpConnectionUtil.profiles = profiles;

    }



    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static void setSharedPreferences(SharedPreferences sharedPreferences) {
        HttpConnectionUtil.sharedPreferences = sharedPreferences;
    }

    static SharedPreferences sharedPreferences;

    public static void clearAllPreferences(SQLiteDatabase db, SharedPreferences sharedPreferences) {

        sharedPreferences.edit().clear().commit();
        //Clear all tables
        db.execSQL("delete from "+ HandbookContract.ProfileEntry.TABLE_NAME);
        db.execSQL("delete from "+ HandbookContract.TimetableEntry.TABLE_NAME);

    }



    public static int getMessageType(String msgType) {
        if(msgType.equals(MsgType.DIARY_NOTE.toString()))
            return DIARY_NOTE_TYPE;
        else if(msgType.equals(MsgType.HOMEWORK.toString()))
            return HOMEWORK_TYPE;
        else if(msgType.equals(MsgType.PARENT_NOTE.toString()))
            return PARENT_NOTE_TYPE;
        else

            return OTHER_NOTE_TYPE;
            
    }

    public enum RESTMethod {
        GET,
        POST,
        PUT,
        DELETE
    };

    public enum ViewType{
        SUMMARY,
        DETAIL
    }

    public static String URL_ENPOINT = "http://schoollink.co.in";
    //public static String URL_ENPOINT ="https://floating-bastion-86283.herokuapp.com";
    public static int GCM_NOTIFICATION = 1000;
    public static int EVENT_NOTIFICATION = 2000;
    public static int TIMETABLE_NOTIFICATION = 3000;


    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     *
     * @param urlString A string representation of a URL.
     * @return An String containing the received response.
     */
    public String downloadUrl(String urlString, RESTMethod method, JSONObject inputJson) {
        // BEGIN_INCLUDE(get_inputstream)
        String response = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod(method.toString());
            if (inputJson != null) {

                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(inputJson.toString());
                wr.flush();
                wr.close();
            } else {
                //conn.setDoInput(true);
                //InputStream in = conn.getInputStream();

            }
            InputStream stream = conn.getInputStream();
            int result = conn.getResponseCode();
            if (result == HttpURLConnection.HTTP_OK) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = stream.read(buffer)) > 0) {
                    os.write(buffer, 0, bytesRead);
                }
                response = os.toString("UTF-8");
                os.close();
            }
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        return response;
    }

    //static String mobileNumber = "9343603060";
    static String mobileNumber = "9611696804";

    public static String getMobileNumber() {
        //String number ="9611696804";
        //return number;
        return mobileNumber;
    }

    static String selectedProfileId;

    public static String getSelectedProfileId()
    {

        selectedProfileId= sharedPreferences.getString(QuickstartPreferences.SELECTED_PROFILE_ID,"");
        return selectedProfileId;
    }

    public static void setSelectedProfileId(String profileId)
    {
        selectedProfileId = profileId;
        sharedPreferences.edit().putString(QuickstartPreferences.SELECTED_PROFILE_ID,selectedProfileId).commit();

    }

    public interface FileUploadService {
        @Multipart
        @POST("uploadTeacherOrStudentImage")
        Call<ImageUploadResponse> uploadTeacherOrStudentImage(@Part("description") RequestBody description,
                                  @Part MultipartBody.Part file);
    }

    public interface SchoolService{
        @GET("school/{id}")
        Call<SchoolProfile> getSchoolProfile(@Path("id") String id);

    }

    public interface SchoolCalendarService{
        @GET("Events")
        Call<List<Event>> getSchoolCalendar();

    }

    public interface TimeTableService {
        @GET("StudentTimeTable/{id}")
        Call<TimeTable> getStudentTimeTable(@Path("id") String id);

        @GET("TeacherTimeTable/{id}")
        Call<TeacherTimeTable> getTeacherTimeTable(@Path("id") String id);

    }

    public static String UploadImage(File fileToTranser)
    {
        String response=null;
        imageUploaded=false;
        FileUploadService service =
                ServiceGenerator.createService(FileUploadService.class);

        RequestBody fBody = RequestBody.create(MediaType.parse("multipart/form-data"), fileToTranser);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", fileToTranser.getName(), fBody);

        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);

/*
 RequestBody id = RequestBody.create(MediaType.parse("text/plain"), AZUtils.getUserId(this));*/
        Call<ImageUploadResponse> call = service.uploadTeacherOrStudentImage(description, body);
        /*try {
            ImageUploadResponse imageResponse =call.execute().body();
            imageUploadStatus=true;
            imageUrl=imageResponse.getImageUrl();


        } catch (IOException e) {
            imageUploadStatus=false;
            e.printStackTrace();
            imageUploadStatus=false;
        }
        finally {
            imageUploaded=true;
        }*/

        call.enqueue(new Callback<ImageUploadResponse>() {

            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {

                if (response.isSuccessful()) {
                    //String resp = response.message();

                    try {
                        //imageUrl= response.body().string();
                        imageUrl= response.body().getImageUrl();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imageUploadStatus=true;


                }
                else {
                    imageUploadStatus = false;
                }
                Log.d(TAG,"Received response after sending file"+response);
                imageUploaded=true;
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                Log.d(TAG,"Sending file failure"+t);
                call.isExecuted();
                imageUploaded=true;
                imageUploadStatus=false;

            }
        });

        return response;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){

            return false;
        }
        return true;
    }


    public static void launchHomePage(Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("position", 0);
        context.startActivity(intent);
    }

    public static void setMobilenumber(String number) {
        mobileNumber = number;
    }

    public static String getPhotoFileName() {
        Calendar c = Calendar.getInstance();
        int currentTime = c.get(Calendar.DATE);
        String name= "IMG_" + DateFormat.getDateTimeInstance().format(new Date()) + ".jpg";
        name =name.replace(' ','_');
        return name;

    }

    public static File getPhotoFile(Context ctx, String name) {
        File externalFilesDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, name);


    }
}
