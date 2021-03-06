package com.myapp.handbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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

import static com.myapp.handbook.domain.SchoolProfile.saveSchooProfileToDB;

/**
 * Created by Ashutosh on 10/16/2017.
 */

public class SchoolContactsFragment extends Fragment {
    public static final String LOG_TAG = SchoolContactsFragment.class.getSimpleName();
    View contactView;
    SchoolProfile schoolProfile;
    SQLiteDatabase db;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contactView = inflater.inflate(R.layout.fragment_schoolcontact, container, false);

        SQLiteOpenHelper handbookDbHelper = new HandBookDbHelper(inflater.getContext());

        db = handbookDbHelper.getReadableDatabase();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (!sharedPreferences.getBoolean(QuickstartPreferences.SCHOOL_PROFILE_DOWNLOADED, false)) {
            //Download the profile
            new FetchSchoolProfileAsyncTask().execute();
            Log.v("SchoolContact", "Saving in db");

        } else {
            schoolProfile = HandBookDbHelper.loadSchoolProfileFromDB(db);
            Log.v("SchoolContact", "Loading from DB");
            UpdateView();
        }

        setHasOptionsMenu(true);

        return contactView;
    }

    /* @Override
     public void onPrepareOptionsMenu(Menu menu) {

         MenuItem item=menu.findItem(R.id.action_search);
         item.setVisible(false);
         super.onPrepareOptionsMenu(menu);
     }
 */
    private void UpdateView() {

        if (schoolProfile != null) {


            TextView schoolName = (TextView) contactView.findViewById(R.id.contact_school_name);
            TextView schoolAddress1 = (TextView) contactView.findViewById(R.id.contact_school_Address_1);
            TextView schoolAddress2 = (TextView) contactView.findViewById(R.id.contact_school_Address_2);
            TextView schoolAddress3 = (TextView) contactView.findViewById(R.id.contact_school_Address_3);
            TextView schoolWebsite = (TextView) contactView.findViewById(R.id.school_website);
            TextView schoolBrochure = (TextView) contactView.findViewById(R.id.school_brochure);
            TextView schoolPrimaryPhoneNumber = (TextView) contactView.findViewById(R.id.school_primary_contact_number);
            TextView schoolSecondaryPhoneNumber = (TextView) contactView.findViewById(R.id.school_secondary_contact_number);
            TextView schoolEmail = (TextView) contactView.findViewById(R.id.school_contact_email);
            ImageView schoolLogo = (ImageView) contactView.findViewById(R.id.school_contact_logo);
            ImageView schoolAddress2Img = (ImageView) contactView.findViewById(R.id.ic_contact_address2);
            ImageView schoolAddress3Img = (ImageView) contactView.findViewById(R.id.ic_contact_address3);
            ImageView schoolPrimaryContactNumberImg = (ImageView) contactView.findViewById(R.id.ic_phone_contact1);
            ImageView schoolSecondaryContactNumberImg = (ImageView) contactView.findViewById(R.id.ic_phone_contact2);
            ImageView schoolEmailIdImg = (ImageView) contactView.findViewById(R.id.ic_email_contact);
            ImageView schoolEmaImg = (ImageView) contactView.findViewById(R.id.ic_email_contact);

            Glide.with(getContext())
                    .load(schoolProfile.getSchoolLogoImageURL())
                    .placeholder(R.drawable.contact_picture_placeholder)
                    //.error(R.drawable.contact_picture_error)
                    //.override(120,120)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(schoolLogo);
            schoolName.setText(schoolProfile.getSchoolName());
            schoolAddress1.setText(schoolProfile.getSchoolFullAddress());
            if (schoolProfile.getSchoolAddress2() != null
                    || schoolProfile.getSchoolAddress3() != null
                    || schoolProfile.getSchoolSecondaryTelephoneNumber() != null
                    || schoolProfile.getSchoolEmailId() != null) {
                schoolAddress2.setText(schoolProfile.getSchoolAddress2());
                schoolAddress3.setText(schoolProfile.getSchoolAddress3());
                schoolSecondaryPhoneNumber.setText(schoolProfile.getSchoolSecondaryTelephoneNumber());
                schoolEmail.setText(schoolProfile.getSchoolEmailId());

            } else {
                schoolAddress2.setVisibility(View.GONE);
                schoolAddress2Img.setVisibility(View.GONE);
                schoolAddress3.setVisibility(View.GONE);
                schoolAddress3Img.setVisibility(View.GONE);
                schoolSecondaryPhoneNumber.setVisibility(View.GONE);
                schoolSecondaryContactNumberImg.setVisibility(View.GONE);
                schoolEmail.setVisibility(View.GONE);
                schoolEmailIdImg.setVisibility(View.GONE);

            }


            schoolPrimaryPhoneNumber.setText(schoolProfile.getSchoolMainTelephoneNumber());
            final String schoolPrimaryContact = schoolProfile.getSchoolMainTelephoneNumber();

            schoolPrimaryContactNumberImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + schoolPrimaryContact));
                    startActivity(intent);

                }
            });

            schoolSecondaryPhoneNumber.setText(schoolProfile.getSchoolSecondaryTelephoneNumber());

            //Code to make Website Clickable
            schoolWebsite.setClickable(true);
            String websiteText = schoolProfile.getSchoolWebSite();
            schoolWebsite.setMovementMethod(LinkMovementMethod.getInstance());
            String schoolUrl = "<a href ='" + websiteText + "'>" + websiteText + "</a>";
            schoolWebsite.setText(Html.fromHtml(schoolUrl));


        }
    }

    private class FetchSchoolProfileAsyncTask extends AsyncTask<Void, Void, SchoolProfile> {
        @Override
        protected SchoolProfile doInBackground(Void... params) {
            HttpConnectionUtil.SchoolService schoolService = ServiceGenerator.createService(HttpConnectionUtil.SchoolService.class);

            //Get the id of teacher or student
            List<RoleProfile> profiles = HandBookDbHelper.LoadProfilefromDb(db);
            //TO-DO Hardcoded to 0th indexed need to be changed based on currently selected profile
            //String id = profiles.get(0).getId();
            String id = "100";
            Call<SchoolProfile> call = schoolService.getSchoolProfile(id);
            try {
                return call.execute().body();
            } catch (IOException e) {
                Log.d("SchoolContact", "Error in fetching school profile");
            }

            return null;
        }

        @Override
        protected void onPostExecute(SchoolProfile profile) {

            schoolProfile = profile;
            //TO-DO Store school profile to DB
            if (schoolProfile != null) {
                saveSchooProfileToDB(schoolProfile, db, sharedPreferences);
            }
            UpdateView();

        }

    }
}