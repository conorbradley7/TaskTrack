package com.example.tasktrack;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;

public class Day implements Serializable {
    int startHour, finishHour;
    ArrayList<TaskObj> todayTasks;
    ArrayList<Integer> breaks;

    ArrayList<HourBin> hourBins;

    public Day(ArrayList<TaskObj> dayTasks){
        this.todayTasks = dayTasks;
        this.hourBins = new ArrayList<>();

    }

    public void scheduleBins(){
        for (int i = 0; i<this.todayTasks.size(); i++){
            //PICK AN HOUR BIN BEST FIT ALGORITHM
            HourBin selectedBin = null;
            int binRemainingSpace = 60;

            for (int j=0; j<this.hourBins.size(); j++){
                if (this.hourBins.get(j).doesTaskFit(this.todayTasks.get(i))){
                    if (hourBins.get(j).getCapacity()- Integer.valueOf(todayTasks.get(i).getExpDur()) < binRemainingSpace){
                        selectedBin = hourBins.get(j);
                        binRemainingSpace = hourBins.get(j).getCapacity()- Integer.valueOf(todayTasks.get(i).getExpDur());
                    }
                }
            }
            if (selectedBin == null){
                HourBin newBin = new HourBin();
                newBin.assignTaskToBin(this.todayTasks.get(i));
                this.hourBins.add(newBin);
            }
            else{
                selectedBin.assignTaskToBin(this.todayTasks.get(i));
            }
        }
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getFinishHour() {
        return finishHour;
    }

    public void setFinishHour(int finishHour) {
        this.finishHour = finishHour;
    }

    public ArrayList<TaskObj> getTodayTasks() {
        return todayTasks;
    }

    public void setTodayTasks(ArrayList<TaskObj> todayTasks) {
        this.todayTasks = todayTasks;
    }

    public ArrayList<Integer> getBreaks() {
        return breaks;
    }

    public void setBreaks(ArrayList<Integer> breaks) {
        this.breaks = breaks;
    }

    public ArrayList<HourBin> getHourBins() {
        return hourBins;
    }

    public void setHourBins(ArrayList<HourBin> hourBins) {
        this.hourBins = hourBins;
    }
}
