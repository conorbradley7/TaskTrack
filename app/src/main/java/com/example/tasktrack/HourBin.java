package com.example.tasktrack;

import java.io.Serializable;
import java.util.ArrayList;

public class HourBin implements Serializable {

    int capacity, hour;
    ArrayList<TaskObj> hourTasks;

    public HourBin(){
        this.capacity = 60;
        this.hourTasks = new ArrayList<>();
    }

    public boolean doesTaskFit(TaskObj task){
        int time = 20;
        if (task.getExpDur() != ""){time =  Integer.valueOf(task.getExpDur());}
        if (time <= this.capacity){
            return true;
        }
        return false;
    }

    public void assignTaskToBin(TaskObj task){
        int time = 20;
        if (task.getExpDur() != ""){time =  Integer.valueOf(task.getExpDur());}
        this.capacity -= time;
        this.hourTasks.add(task);
    }

    public String getStrTasks(){
        String outstr = "";
        for (int i = 0; i<this.hourTasks.size();i++){
            outstr+=this.hourTasks.get(i).getTitle();
            if (i == hourTasks.size()-1){
                continue;
            }
            else{
                outstr+=", ";
            }
        }

        return outstr;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public ArrayList<TaskObj> getHourTasks() {
        return hourTasks;
    }

    public void setHourTasks(ArrayList<TaskObj> hourTasks) {
        this.hourTasks = hourTasks;
    }
}

