package com.myapp.handbook;

import com.myapp.handbook.data.HandbookContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAshutosh on 6/12/2016.
 */

public class Profile {

    public enum ProfileRole{
        STUDENT,
        TEACHER,
        PARENT,
        PRINCIPAL
    };


    public static final String STUDENT_FIRST_NAME ="StudentFirstName";
    public static final String STUDENT_ID ="StudentId";
    public static final String STUDENT_MIDDLE_NAME ="StudentMiddleName";
    public static final String STUDENT_LAST_NAME ="StudentLastName";
    public static final String STUDENT_DOB ="StudentDOB";
    public static final String STUDENT_GENDER ="StudentGender";
    public static final String STUDENT_STD ="StudentClassStandard";
    public static final String STUDENT_ADDRESS ="StudentFullAddress";

    public static final String TEACHER_FIRST_NAME ="TeacherFirstName";
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

    public Profile(String id,String firstName, String middleName, String lastName, String role, String gender, String birth_date, String std, String address){
        this.id = id;
        this.address=address;
        this.birth_date=birth_date;
        this.firstName=firstName;
        this.lastName=lastName;
        this.gender=gender;
        this.role=role;
        this.std=std;
        this.middleName=middleName;
    }

    public Profile(){

    }

    public static Profile parseStudentJSonObject(JSONObject studentObj) throws JSONException {

        Profile studentProfile = null;
        if(studentObj!=null) {

            studentProfile = new Profile();
            studentProfile.setId(studentObj.getString(STUDENT_ID));
            studentProfile.setRole(ProfileRole.STUDENT.toString());
            studentProfile.setFirstName(studentObj.getString(STUDENT_FIRST_NAME));
            studentProfile.setMiddleName(studentObj.getString(STUDENT_MIDDLE_NAME));
            studentProfile.setLastName(studentObj.getString(STUDENT_LAST_NAME));
            studentProfile.setGender(studentObj.getString(STUDENT_GENDER));
            studentProfile.setStd(studentObj.getString(STUDENT_STD));
            studentProfile.setBirth_date(studentObj.getString(STUDENT_DOB));
        }
        return studentProfile;

    }

    public static Profile parseTeacherJSonObject(JSONObject teacherObj) throws JSONException {

        Profile studentProfile = new Profile();
        studentProfile.setRole(ProfileRole.TEACHER.toString());
        studentProfile.setFirstName(teacherObj.getString(TEACHER_FIRST_NAME));
        studentProfile.setMiddleName(teacherObj.getString(TEACHER_MIDDLE_NAME));
        studentProfile.setLastName(teacherObj.getString(TEACHER_LAST_NAME));
        studentProfile.setGender(teacherObj.getString(TEACHER_GENDER));
        studentProfile.setStd("");
        studentProfile.setBirth_date(teacherObj.getString(TEACHER_DOB));

        return studentProfile;

    }

    public static boolean storeProfile(List<Profile> profiles){

        return true;
    }

    public static List<Profile> loadProfile(){

        List<Profile> profiles = new ArrayList<>();


        return profiles;
    }


}
