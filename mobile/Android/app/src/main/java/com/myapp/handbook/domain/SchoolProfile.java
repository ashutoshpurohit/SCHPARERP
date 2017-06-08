package com.myapp.handbook.domain;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.myapp.handbook.QuickstartPreferences;
import com.myapp.handbook.data.HandBookDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sashutosh on 9/3/2016.
 */
public class SchoolProfile {

    public static final String SCHOOL_ID = "SchoolId";
    public static final String SCHOOL_NAME ="SchoolFullName";
    public static final String SCHOOL_ADDRESS_1= "SchoolFullAddress";
    public static final String SCHOOL_ADDRESS_2= "SchoolAddress2";
    public static final String SCHOOL_ADDRESS_3= "SchoolAddress3";
    public static final String SCHOOL_PHONE_1 ="SchoolMainTelephoneNumber";
    public static final String SCHOOL_PHONE_2 ="SchoolSecondaryTelephoneNumber";
    public static final String SCHOOL_EMAIL_ID ="SchoolEmailId";
    public static final String SCHOOL_WEBSITE ="SchoolWebSite";
    public static final String LOGO_IMAGE_URL = "ImageUrlLogo";



    String SchoolId;
    String SchoolFullName;
    String ImageUrlLogo;
    String SchoolMainTelephoneNumber;
    String SchoolSecondaryTelephoneNumber;
    String SchoolFullAddress;
    String SchoolAddress_2;
    String SchoolAddress_3;
    String SchoolEmailId;
    String SchoolWebSite;

   /* public SchoolProfile(String id, String schoolName, String schoolAddress1, String schoolAddress2,
                         String schoolAddress3, String schoolPrimaryContact, String schoolSecondaryContact,
                         String schoolEmailId, String schoolWebsite, String schoolLogo) {

        this.SchoolId = id;
        this.SchoolFullName = schoolName;
        this.SchoolAddress_1 = schoolAddress1;
        this.SchoolAddress_2 = schoolAddress2;
        this.SchoolAddress_3 = schoolAddress3;
        this.SchoolMainTelephoneNumber = schoolPrimaryContact;
        this.SchoolSecondaryTelephoneNumber = schoolSecondaryContact;
        this.SchoolEmailId = schoolEmailId;
        this.SchoolWebSite = schoolWebsite;
        this.ImageUrlLogo = schoolLogo;
    }
*/


    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        this.SchoolId = schoolId;
    }
    //Getter and Setter method for School Name
    public String getSchoolName() {
        return SchoolFullName;
    }

    public void setSchoolName(String schoolName) {
        this.SchoolFullName = schoolName;
    }


    //Getter and Setter method for School Address Line1
    public String getSchoolFullAddress() {
        return SchoolFullAddress;
    }
    public void setSchoolFullAddress(String schoolFullAddress) {
        this.SchoolFullAddress = schoolFullAddress;
    }


    //Getter and Setter method for School Address Line2
    public String getSchoolAddress2() {
        return SchoolAddress_2;
    }
    public void setSchoolAddress_2(String schoolAddress_2) {
        this.SchoolAddress_2 = schoolAddress_2;
    }

    //Getter and Setter method for School Address Line3
    public String getSchoolAddress3() {
        return SchoolAddress_3;
    }

    public void setSchoolAddress_3(String schoolAddress_3) {
        this.SchoolAddress_3 = schoolAddress_3;
    }

    //Getter and Setter method for School Logo
    public String getSchoolLogoImageURL() {
        return ImageUrlLogo;
    }

    public void setSchoolLogoImageURL(String schoolLogoImageURL) {
        this.ImageUrlLogo = schoolLogoImageURL;
    }

    //Getter and Setter method for School Main Contact Number
    public String getSchoolMainTelephoneNumber() {
        return SchoolMainTelephoneNumber;
    }

    public void setSchoolMainTelephoneNumber(String schoolMainTelephoneNumber) {
        this.SchoolMainTelephoneNumber = schoolMainTelephoneNumber;
    }

    //Getter and Setter method for School Main Contact Number
    public String getSchoolSecondaryTelephoneNumber() {
        return SchoolSecondaryTelephoneNumber;
    }

    public void setSchoolSecondaryTelephoneNumber(String schoolSecondaryTelephoneNumber) {
        this.SchoolSecondaryTelephoneNumber = schoolSecondaryTelephoneNumber;
    }

    //Getter and Setter method for School Email id
    public String getSchoolEmailId() {
        return SchoolEmailId;
    }
    public void setSchoolEmailId(String schoolEmailId) {
        this.SchoolEmailId = schoolEmailId;
    }

    //Getter and Setter method for School Website
    public String getSchoolWebSite() {
        return SchoolWebSite;
    }

    public void setSchoolWebSite(String schoolWebSite) {
        this.SchoolWebSite = schoolWebSite;
    }
    public static void saveSchooProfiletoDB(SchoolProfile schoolProfile, SQLiteDatabase db, SharedPreferences sharedPreferences ) {
        HandBookDbHelper.insertSchoolContactEntry(db,schoolProfile.getSchoolId(),schoolProfile.getSchoolName(),
                schoolProfile.getSchoolFullAddress(),schoolProfile.getSchoolAddress2(),schoolProfile.getSchoolAddress3(),
                schoolProfile.getSchoolMainTelephoneNumber(),schoolProfile.getSchoolSecondaryTelephoneNumber(),
                schoolProfile.getSchoolEmailId(),schoolProfile.getSchoolWebSite(),schoolProfile.getSchoolLogoImageURL());

        sharedPreferences.edit().putBoolean(QuickstartPreferences.SCHOOL_PROFILE_DOWNLOADED, true).commit();

    }



    public static SchoolProfile parseJSonObject(JSONObject jsonObj) throws JSONException {

        SchoolProfile schoolProfile = null;
        if(jsonObj!=null) {

            schoolProfile = new SchoolProfile();
            schoolProfile.setSchoolId(SCHOOL_ID);
            schoolProfile.setSchoolLogoImageURL(jsonObj.getString(LOGO_IMAGE_URL));
            schoolProfile.setSchoolName(jsonObj.getString(SCHOOL_NAME));
            schoolProfile.setSchoolFullAddress(jsonObj.getString(SCHOOL_ADDRESS_1));
            schoolProfile.setSchoolAddress_2(jsonObj.getString(SCHOOL_ADDRESS_2));
            schoolProfile.setSchoolAddress_3(jsonObj.getString(SCHOOL_ADDRESS_3));
            schoolProfile.setSchoolMainTelephoneNumber(jsonObj.getString(SCHOOL_PHONE_1));
            schoolProfile.setSchoolSecondaryTelephoneNumber(jsonObj.getString(SCHOOL_PHONE_2));
            schoolProfile.setSchoolWebSite(jsonObj.getString(SCHOOL_WEBSITE));
            schoolProfile.setSchoolEmailId(jsonObj.getString(SCHOOL_EMAIL_ID));

        }
        return schoolProfile;
    }

}
