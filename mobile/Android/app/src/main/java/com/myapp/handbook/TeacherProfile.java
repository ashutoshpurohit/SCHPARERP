package com.myapp.handbook;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.myapp.handbook.data.HandBookDbHelper;
import com.myapp.handbook.domain.RoleProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by SAshutosh on 6/15/2016.
 */
public class TeacherProfile
{
    public static final String TEACHER_FIRST_NAME ="TeacherFirstName";
    public static final String TEACHER_ID ="TeacherId";
    public static final String TEACHER_LAST_NAME ="TeacherLastName";
    public static final String TEACHER_MOBILE ="MobileNumber";
    public static final String TEACHER_EMAIL ="EmailId";
    public static final String TEACHER_ROLE_STD ="RoleforStd";
    public static final String TEACHER_SUBJECT ="RoleforSubject";

    public TeacherProfile() {

    }


    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getStd() {
        return std;
    }

    public String getSubject() {
        return subject;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStd(String std) {
        this.std = std;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    String id;
    String firstName;
    String lastName;
    String mobileNumber;
    String email;
    String std;
    String subject;

    public TeacherProfile(String id, String firstName, String lastName, String mobileNumber, String email, String std, String subject) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.std = std;
        this.subject = subject;
    }
    public static TeacherProfile parseTeacherObject(JSONObject teacherObject){

        TeacherProfile profile=null;
        String lastName="";
        String email="";
        String std="";
        try {
            String firstName = teacherObject.getString(TEACHER_FIRST_NAME);
            String id = teacherObject.getString(TEACHER_ID);
            if(teacherObject.has(TEACHER_LAST_NAME)) {
                lastName = teacherObject.getString(TEACHER_LAST_NAME);
            }
            String mobile = teacherObject.getString(TEACHER_MOBILE);
            if(teacherObject.has(TEACHER_EMAIL)) {
                 email = teacherObject.getString(TEACHER_EMAIL);
            }
            if(teacherObject.has(TEACHER_ROLE_STD)) {
             std=teacherObject.getString(TEACHER_ROLE_STD);
            }
            String subject = teacherObject.getString(TEACHER_SUBJECT);

             profile= new TeacherProfile(id,firstName,lastName,mobile,email,std,subject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return profile;

    }

    public static void saveTeacherListToDB(String studentId, List<TeacherProfile> profiles, SQLiteDatabase db, SharedPreferences sharedPreferences ) {

        for(TeacherProfile profile:profiles) {

            HandBookDbHelper.insertTeacherForStudent(db,studentId,profile.getId(),profile.getFirstName(),profile.getLastName(),profile.getMobileNumber(),profile.getEmail(),profile.getSubject(),profile.getStd());
        }

        sharedPreferences.edit().putBoolean(QuickstartPreferences.STUDENT_TEACHER_DOWNLOADED + "_"+ studentId, true).commit();

    }


}
