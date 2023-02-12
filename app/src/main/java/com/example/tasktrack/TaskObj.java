package com.example.tasktrack;

import java.io.Serializable;

public class TaskObj implements Serializable {

    //Initial OnCreation Attributes
    private String title, moreDetails, tag, date, expDur, priority;

    //On Completion Attributes... init => false/null
    private Boolean started, completed, incomplete;
    private String startTime, finishTime, actualDur, difficulty, id;


    public TaskObj(String id, String title, String moreDetails, String tag, String date, String expDur, String priority, Boolean started){
        this.id = id;
        this.title = title;
        this.moreDetails = moreDetails;
        this.tag = tag;
        this.date = date;
        this.expDur = expDur;
        this.priority = priority;

        this.started = started;
        this.completed = false;
        this.incomplete = false;
        this.startTime = null;
        this.finishTime = null;
        this.actualDur = null;
        this.difficulty = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getExpDur() { return expDur; }

    public void setExpDur(String expDur) { this.expDur = expDur; }

    public String getPriority() { return priority; }

    public void setPriority(String priority) { this.priority = priority; }

    public Boolean getStarted() {
        return started;
    }

    public void setStarted(Boolean started) {
        this.started = started;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getIncomplete() {
        return incomplete;
    }

    public void setIncomplete(Boolean incomplete) {
        this.incomplete = incomplete;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getActualDur() {
        return actualDur;
    }

    public void setActualDur(String actualDur) {
        this.actualDur = actualDur;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}