package com.example.tasktrack;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    public String email, name, dob, gender;
    public ArrayList<String> tags;
    //TODO
    //profile pic

    public User(String email, String name, String dob, String gender){
        this.email = email;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.tags = tags;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
