package com.myapp.handbook;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by SAshutosh on 6/7/2016.
 */
public class HttpConnectionUtil {

    private static final String TAG = "HttConnectionUtil";
    public static boolean imageUploaded =false;
    public static boolean imageUploadStatus =false;
    public static String imageUrl="";
    public enum RESTMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    ;

    public static String URL_ENPOINT = "https://floating-bastion-86283.herokuapp.com";
    public static int GCM_NOTIFICATION = 1000;

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
            }
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        return response;
    }

    static String mobileNumber = "9611696804";

    public static String getMobileNumber() {
        //String number ="9611696804";
        //return number;
        return mobileNumber;
    }

    public interface FileUploadService {
        @Multipart
        @POST("uploadTeacherOrStudentImage")
        Call<ResponseBody> uploadTeacherOrStudentImage(@Part("description") RequestBody description,
                                  @Part MultipartBody.Part file);
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
        Call<ResponseBody> call = service.uploadTeacherOrStudentImage(description, body);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                imageUploaded=true;
                if (response.isSuccessful()) {
                    //String resp = response.message();

                    try {
                        //imageUrl= response.body().string();
                        imageUrl= response.body().toString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imageUploadStatus=true;


                }
                else {
                    imageUploadStatus = false;
                }
                Log.d(TAG,"Received response after sending file"+response);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Sending file failure"+t);
                call.isExecuted();
                imageUploaded=true;
                imageUploadStatus=false;

            }
        });


        return response;
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
