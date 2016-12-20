package com.myapp.handbook.domain;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.myapp.handbook.QuickstartPreferences;
import com.myapp.handbook.data.HandBookDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAshutosh on 6/12/2016.
 */

public class RoleProfile implements Parcelable {

    private static final String TAG = "ProfileParse Fetch";
    protected RoleProfile(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        middleName = in.readString();
        lastName = in.readString();
        role = in.readString();
        gender = in.readString();
        birth_date = in.readString();
        std = in.readString();
        address = in.readString();
        mobileNumber = in.readString();
    }

        public static final Creator<RoleProfile> CREATOR = new Creator<RoleProfile>() {
        @Override
        public RoleProfile createFromParcel(Parcel in) {
            return new RoleProfile(in);
        }

        @Override
        public RoleProfile[] newArray(int size) {
            return new RoleProfile[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(middleName);
        dest.writeString(lastName);
        dest.writeString(role);
        dest.writeString(gender);
        dest.writeString(birth_date);
        dest.writeString(std);
        dest.writeString(address);
        dest.writeString(mobileNumber);
    }

    public enum ProfileRole{
        STUDENT,
        TEACHER,
        PARENT,
        PRINCIPAL
    };


    public static final String STUDENT_FIRST_NAME ="StudentFirstName";
    public static final String FATHER_MOBILE = "FatherMobile";
    public static final String STUDENT_FULL_NAME ="StudentFullName";
    public static final String STUDENT_ID ="StudentId";
    public static final String STUDENT_MIDDLE_NAME ="StudentMiddleName";
    public static final String STUDENT_LAST_NAME ="StudentLastName";
    public static final String STUDENT_DOB ="StudentDOB";
    public static final String STUDENT_GENDER ="StudentGender";
    public static final String STUDENT_STD ="StudentClassStandard";
    public static final String STUDENT_ADDRESS ="StudentFullAddress";
    public static final String STUDENT_MOBILE ="StudentParentMobiles";
    public static final String IMAGE_URL="ImageUrl";

    public static final String TEACHER_FIRST_NAME ="TeacherFirstName";
    public static final String TEACHER_ID ="TeacherId";
    public static final String TEACHER_MIDDLE_NAME ="TeacherMiddleName";
    public static final String TEACHER_LAST_NAME ="TeacherLastName";
    public static final String TEACHER_DOB ="TeacherDOB";
    public static final String TEACHER_GENDER ="TeacherGender";
    public static final String TEACHER_ADDRESS ="TeacherFullAddress";


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public ProfileRole getProfileRole(){
        if (role.equals(ProfileRole.STUDENT.toString()))
            return ProfileRole.STUDENT;
        else if(role.equals(ProfileRole.TEACHER.toString()))
            return ProfileRole.TEACHER;
        else {
            return ProfileRole.PRINCIPAL;
        }
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getStd() {
        return std;
    }

    public void setStd(String std) {
        this.std = std;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String id;
    private  String firstName;

    private String middleName;
    private  String lastName;

    // Weather id as returned by API, to identify the icon to be used
    private String role;

    // Short description and long description of the weather, as provided by API.
    // e.g "clear" vs "sky is clear".
    private String gender;

    private String birth_date;

    // Min and max temperatures for the day (stored as floats)
    private String std;
    private String address;


    private String imageUrl;



    private String mobileNumber;
    public RoleProfile(String id, String firstName, String middleName, String lastName, String role, String gender, String birth_date, String std, String address, String imageUrl){
        this.id = id;
        this.address=address;
        this.birth_date=birth_date;
        this.firstName=firstName;
        this.lastName=lastName;
        this.gender=gender;
        this.role=role;
        this.std=std;
        this.middleName=middleName;
        this.imageUrl=imageUrl;
    }

    public RoleProfile(){

    }

    public static RoleProfile parseStudentJSonObject(JSONObject studentObj) throws JSONException {

        RoleProfile studentProfile = null;
        if(studentObj!=null) {

            studentProfile = new RoleProfile();
            studentProfile.setId(studentObj.getString(STUDENT_ID));
            studentProfile.setRole(ProfileRole.STUDENT.toString());
            studentProfile.setFirstName(studentObj.getString(STUDENT_FIRST_NAME));
            studentProfile.setMiddleName(studentObj.getString(STUDENT_MIDDLE_NAME));
            studentProfile.setLastName(studentObj.getString(STUDENT_LAST_NAME));
            studentProfile.setGender(studentObj.getString(STUDENT_GENDER));
            studentProfile.setStd(studentObj.getString(STUDENT_STD));
            studentProfile.setBirth_date(studentObj.getString(STUDENT_DOB));
            JSONArray mobiles = studentObj.getJSONArray(STUDENT_MOBILE);
            //TO-DO currently we are fetching only first mobile number need to discuss how to implement this
            studentProfile.setMobileNumber(mobiles.get(0).toString());
            studentProfile.setImageUrl(studentObj.getString(IMAGE_URL));
        }
        return studentProfile;

    }

    public static RoleProfile parsePartialStudentJSonObject(JSONObject studentObj) throws JSONException {

        RoleProfile studentProfile = null;
        if(studentObj!=null) {

            studentProfile = new RoleProfile();
            studentProfile.setId(studentObj.getString(STUDENT_ID));
            studentProfile.setRole(ProfileRole.STUDENT.toString());
            studentProfile.setFirstName(studentObj.getString(STUDENT_FULL_NAME));
            studentProfile.setMobileNumber(studentObj.getString(FATHER_MOBILE));

        }
        return studentProfile;

    }

    public static RoleProfile parseTeacherJSonObject(JSONObject teacherObj){

        RoleProfile teacherProfile=null;
        try {
            teacherProfile = new RoleProfile();
            teacherProfile.setRole(ProfileRole.TEACHER.toString());
            teacherProfile.setId(teacherObj.getString(TEACHER_ID));
            teacherProfile.setFirstName(teacherObj.getString(TEACHER_FIRST_NAME));
            teacherProfile.setMiddleName(teacherObj.getString(TEACHER_MIDDLE_NAME));
            teacherProfile.setLastName(teacherObj.getString(TEACHER_LAST_NAME));
            teacherProfile.setGender(teacherObj.getString(TEACHER_GENDER));
            teacherProfile.setStd("");
            teacherProfile.setBirth_date(teacherObj.getString(TEACHER_DOB));


        }
        catch(JSONException w)
        {
            Log.d(TAG,"JSON for teacher missing or incorrect");
        }
        return teacherProfile;
    }

    public static List<RoleProfile> GetProfileForRole(SQLiteDatabase db, RoleProfile.ProfileRole role)
    {
        List<RoleProfile> profiles= new ArrayList<>();

        List<RoleProfile> allProfiles = HandBookDbHelper.LoadProfilefromDb(db);
        for(RoleProfile profile:allProfiles){
            if(profile.getRole().equalsIgnoreCase(role.toString())){
                profiles.add(profile);
            }
        }
        return  profiles;

    }

    public static void savetoDB(List<RoleProfile> profiles, SQLiteDatabase db, SharedPreferences sharedPreferences ) {

        for(RoleProfile profile:profiles) {

            HandBookDbHelper.insertProfile(db,profile.getId(),profile.getFirstName(),profile.getLastName(),profile.getMiddleName(),profile.getRole(),profile.getGender(),profile.getStd(),profile.getAddress(),profile.getBirth_date(),profile.getImageUrl());
        }

        sharedPreferences.edit().putBoolean(QuickstartPreferences.PROFILE_DOWNLOADED, true).commit();

    }


    public static boolean storeProfile(List<RoleProfile> profiles){

        return true;
    }

    public static List<RoleProfile> loadProfile(){

        List<RoleProfile> profiles = new ArrayList<>();


        return profiles;
    }

    public static RoleProfile getProfile(SQLiteDatabase db,String profileId) {

        List<RoleProfile> profiles = HandBookDbHelper.LoadProfilefromDb(db);
        for(int i=0;i< profiles.size();i++){
            if(profiles.get(i).getId().equals(profileId))
                return profiles.get(i);
        }
        return null;
    }


}
