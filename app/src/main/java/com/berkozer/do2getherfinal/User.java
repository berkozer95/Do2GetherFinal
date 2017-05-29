package com.berkozer.do2getherfinal;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class User {

    private String userId;
    private String fullname;
    private String displayname;
    private String email;
    private String age;
    private String gender;
    private String occupation;
    private String university;
    private String currentplaceid;
    private String firebaseToken;
    private String fbPhotoURL;
    private String matchScore;
    private String profiletype;


    private ArrayList<String> hobbies = new ArrayList<String>();

    public User(String userId, String displayname, String firebaseToken) {
        this.userId = userId;
        this.displayname = displayname;
        this.firebaseToken = firebaseToken;
    }

    public User(String userId, String fullName, String displayName, String email, String age, String gender, String occupation, String university, String currentplaceid, ArrayList<String> hobbies,String fbPhotoURL,String matchScore,String profiletype) {
        this.userId = userId;
        this.fullname = fullName;
        this.displayname = displayName;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.occupation = occupation;
        this.university = university;
        this.currentplaceid = currentplaceid;
        this.hobbies = hobbies;
        this.fbPhotoURL = fbPhotoURL;
        this.matchScore = matchScore;
        this.profiletype = profiletype;
    }

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullName) {
        this.fullname = fullName;
    }

    public String getDisplayName() {
        return displayname;
    }

    public void setDisplayName(String displayName) {
        this.displayname = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public ArrayList<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(ArrayList<String> hobbies) {
        this.hobbies = hobbies;
    }

    public String getCurrentplaceid() {
        return currentplaceid;
    }

    public void setCurrentplaceid(String currentplaceid) {
        this.currentplaceid = currentplaceid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFbPhotoURL() {
        return fbPhotoURL;
    }

    public void setFbPhotoURL(String fbPhotoURL) {
        this.fbPhotoURL = fbPhotoURL;
    }

    public String getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(String matchScore) {
        this.matchScore = matchScore;
    }

    public String getProfiletype() {
        return profiletype;
    }

    public void setProfiletype(String profiletype) {
        this.profiletype = profiletype;
    }
}
