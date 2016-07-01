package com.myapp.handbook;

import org.json.JSONException;
import org.json.JSONObject;

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
    public static final String TEACHER_ROLE_TYPE ="RoleType";
    public static final String TEACHER_ROLE_STD ="RoleforStd";
    public static final String TEACHER_SUBJECT ="RoleforSubject";


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
        try {
            String firstName = teacherObject.getString(TEACHER_FIRST_NAME);
            String id = teacherObject.getString(TEACHER_ID);
            String lastName = teacherObject.getString(TEACHER_LAST_NAME);
            String mobile = teacherObject.getString(TEACHER_MOBILE);
            String email = teacherObject.getString(TEACHER_EMAIL);
            String std = teacherObject.getString(TEACHER_ROLE_STD);
            String subject = teacherObject.getString(TEACHER_SUBJECT);

             profile= new TeacherProfile(id,firstName,lastName,mobile,email,std,subject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return profile;

    }


}
