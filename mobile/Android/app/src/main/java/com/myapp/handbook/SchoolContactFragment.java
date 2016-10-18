package com.myapp.handbook;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.RoleProfile;
import com.myapp.handbook.domain.SchoolProfile;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

/**
 * Created by SAshutosh on 10/13/2016.
 */

public class SchoolContactFragment extends Fragment {

    View contactView;
    SchoolProfile schoolProfile;
    SQLiteDatabase db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contactView = inflater.inflate(R.layout.fragment_schoolcontact,container,false);

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());

        db = handbookDbHelper.getReadableDatabase();

        new FetchSchoolProfileAsyncTask().execute();

        return contactView;
    }

    private class FetchSchoolProfileAsyncTask extends AsyncTask<Void, Void, SchoolProfile > {
        @Override
        protected SchoolProfile doInBackground(Void... params) {
            HttpConnectionUtil.SchoolService schoolService = ServiceGenerator.createService(HttpConnectionUtil.SchoolService.class);
            //Get the id of teacher or student
            List<RoleProfile> profiles = HandBookDbHelper.LoadProfilefromDb(db);
            //TO-DO Hardcoded to 0th indexed need to be changed based on currently selected profile
            //String id = profiles.get(0).getId();
            String id ="100";
            Call<SchoolProfile> call = schoolService.getSchoolProfile(id);
            try {
                SchoolProfile schoolProfile = call.execute().body();
                return schoolProfile;
            } catch (IOException e) {
                Log.d("SchoolContact", "Error in fetching school profile");
            }

            return null;
        }

        @Override
        protected void onPostExecute(SchoolProfile profile) {

            schoolProfile=profile;
            //TO-DO Store school profile to DB
            UpdateView();
        }

    }

    private void UpdateView() {

        if(schoolProfile!=null){

            ImageView schoolLogo = (ImageView)contactView.findViewById(R.id.school_contact_logo);
            TextView schoolName = (TextView)contactView.findViewById(R.id.contact_school_name);
            TextView schoolPhoneNumber = (TextView)contactView.findViewById(R.id.school_contact_number);
            TextView schoolEmail = (TextView)contactView.findViewById(R.id.school_contact_email);
            TextView schoolAddress = (TextView)contactView.findViewById(R.id.contact_school_Address);

            Glide.with(getContext())
                    .load(schoolProfile.getSchoolLogoImageURL())
                    .placeholder(R.drawable.contact_picture_placeholder)
                    .error(R.drawable.contact_picture_error)
                    .override(120,120)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(schoolLogo);
            schoolName.setText(schoolProfile.getSchoolName());
            schoolAddress.setText(schoolProfile.getSchoolFullAddress());
            schoolEmail.setText(schoolProfile.getSchoolWebSite());
            schoolPhoneNumber.setText(schoolProfile.getSchoolMainTelephoneNumber());
            schoolPhoneNumber.setText(schoolProfile.getSchoolMainTelephoneNumber());
        }
    }
}
