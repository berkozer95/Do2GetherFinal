package com.berkozer.do2getherfinal.Chat.Model;

import java.util.ArrayList;

/**
 * Created by berkozer on 04/05/2017.
 */

public class User {
    public String uid;
    public String email;
    public String fullname;
    public String firebaseToken;
    public String currentplaceid;
    public ArrayList<String > hobbies;
    public String matchScore;

    public User() {

    }

    public User(String uid, String email, String firebaseToken) {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
    }

    public User(String uid, String email, String fullname, String firebaseToken, String currentplaceid, ArrayList<String> hobbies, String matchScore) {
        this.uid = uid;
        this.email = email;
        this.fullname = fullname;
        this.firebaseToken = firebaseToken;
        this.currentplaceid = currentplaceid;
        this.hobbies = hobbies;
        this.matchScore = matchScore;
    }


    public void setMatchScore(String matchScore) {
        this.matchScore = matchScore;
    }

    public String getMatchScore() {
        return matchScore;
    }
}