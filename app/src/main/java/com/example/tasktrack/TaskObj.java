package com.example.tasktrack;

import java.io.Serializable;

public class TaskObj implements Serializable {

    private String title, moreDetails, tag, date;

    public TaskObj(String title, String moreDetails, String tag, String date){
        this.title = title;
        this.moreDetails = moreDetails;
        this.tag = tag;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMoreDetails() {
        return moreDetails;
    }

    public void setMoreDetails(String moreDetails) {
        this.moreDetails = moreDetails;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}