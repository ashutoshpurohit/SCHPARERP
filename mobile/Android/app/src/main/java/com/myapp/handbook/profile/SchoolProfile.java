package com.myapp.handbook.profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sashutosh on 9/3/2016.
 */
public class SchoolProfile {

    public static final String SCHOOL_NAME ="SchoolFullName";
    public static final String LOGO_IMAGE_URL = "ImageUrlLogo";

    String schoolName;

    public String getSchoolLogoImageURL() {
        return schoolLogoImageURL;
    }

    public void setSchoolLogoImageURL(String schoolLogoImageURL) {
        this.schoolLogoImageURL = schoolLogoImageURL;
    }

    String schoolLogoImageURL;


    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public static SchoolProfile parseJSonObject(JSONObject jsonObj) throws JSONException {

        SchoolProfile schoolProfile = null;
        if(jsonObj!=null) {

            schoolProfile = new SchoolProfile();
            schoolProfile.setSchoolName(jsonObj.getString(SCHOOL_NAME));
            schoolProfile.setSchoolLogoImageURL(jsonObj.getString(LOGO_IMAGE_URL));

        }
        return schoolProfile;

    }

}
