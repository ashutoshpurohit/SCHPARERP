package com.myapp.handbook.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sashutosh on 9/3/2016.
 */
public class SchoolProfile {

    public static final String SCHOOL_NAME ="SchoolFullName";
    public static final String LOGO_IMAGE_URL = "ImageUrlLogo";
    public static final String SCHOOL_ADDRESS= "SchoolFullAddress";
    public static final String SCHOOL_PHONE ="SchoolMainTelephoneNumber";
    public static final String SCHOOL_WEBSITE ="SchoolWebSite";


    String SchoolFullName;

    public String getSchoolLogoImageURL() {
        return ImageUrlLogo;
    }

    public void setSchoolLogoImageURL(String schoolLogoImageURL) {
        this.ImageUrlLogo = schoolLogoImageURL;
    }

    String ImageUrlLogo;
    String SchoolMainTelephoneNumber;

    public String getSchoolMainTelephoneNumber() {
        return SchoolMainTelephoneNumber;
    }

    public void setSchoolMainTelephoneNumber(String schoolMainTelephoneNumber) {
        SchoolMainTelephoneNumber = schoolMainTelephoneNumber;
    }

    public String getSchoolFullAddress() {
        return SchoolFullAddress;
    }

    public void setSchoolFullAddress(String schoolFullAddress) {
        SchoolFullAddress = schoolFullAddress;
    }

    public String getSchoolWebSite() {
        return SchoolWebSite;
    }

    public void setSchoolWebSite(String schoolWebSite) {
        SchoolWebSite = schoolWebSite;
    }

    String SchoolFullAddress;
    String SchoolWebSite;


    public String getSchoolName() {
        return SchoolFullName;
    }

    public void setSchoolName(String schoolName) {
        this.SchoolFullName = schoolName;
    }

    public static SchoolProfile parseJSonObject(JSONObject jsonObj) throws JSONException {

        SchoolProfile schoolProfile = null;
        if(jsonObj!=null) {

            schoolProfile = new SchoolProfile();
            schoolProfile.setSchoolName(jsonObj.getString(SCHOOL_NAME));
            schoolProfile.setSchoolLogoImageURL(jsonObj.getString(LOGO_IMAGE_URL));
            schoolProfile.setSchoolFullAddress(jsonObj.getString(SCHOOL_ADDRESS));
            schoolProfile.setSchoolMainTelephoneNumber(jsonObj.getString(SCHOOL_PHONE));
            schoolProfile.setSchoolWebSite(jsonObj.getString(SCHOOL_WEBSITE));

        }
        return schoolProfile;

    }

}
