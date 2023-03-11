package com.example.tasktrack;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskObj implements Serializable {

    //Initial OnCreation Attributes
    private String title, moreDetails, tag, date, expDur, priority;

    //On Completion Attributes... init => false/null
    private Boolean started, completed, incomplete;
    private String actualDur, difficulty, id;
    LocalDateTime startTime , finishTime;


    public TaskObj(String id, String title, String moreDetails, String tag, String date,
                   String expDur, String priority, Boolean started, Boolean completed,
                   Boolean incomplete, String difficulty, LocalDateTime startTime,
                   LocalDateTime finishTime, String actualDur){
        this.id = id;
        this.title = title;
        this.moreDetails = moreDetails;
        this.tag = tag;
        this.date = date;
        this.expDur = expDur;
        this.priority = priority;

        this.started = started;
        this.completed = completed;
        this.incomplete = incomplete;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.actualDur = actualDur;
        this.difficulty = difficulty;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
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