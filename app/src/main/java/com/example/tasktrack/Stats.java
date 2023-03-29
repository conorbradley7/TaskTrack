package com.example.tasktrack;

import androidx.annotation.NonNull;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Stats {
    public List<DataEntry> completeVsIncompleteData, tagsData, difficultyData, onTimeData, onTimeScatterPoints, onTimeScatterLine;

    public Stats (ArrayList<TaskObj> tasks, ArrayList<String> tags){
        this.completeVsIncompleteData = genCompleteVsIncompletePie(tasks);
        this.tagsData = genTagsPie(tasks, tags);
        this.difficultyData = genDifficultyBarChart(tasks);
        this.onTimeData = genOnTimeData(tasks);
        this.onTimeScatterPoints = genOnTimeScatterPoints(tasks);
        this.onTimeScatterLine = genOnTimeScatterLine(tasks);

    }

    public List<DataEntry> genCompleteVsIncompletePie(@NonNull ArrayList<TaskObj> tasks){
        int compTotal = 0, incompTotal = 0;
        for (int i=0; i<tasks.size(); i++){
            if (tasks.get(i).getCompleted()){
                compTotal += 1;
            }
            else{
                incompTotal += 1;
            }
        }
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Completed", compTotal));
        data.add(new ValueDataEntry("Incomplete", incompTotal));
        return data;
    }

    public List<DataEntry> genTagsPie(@NonNull ArrayList<TaskObj> tasks, ArrayList<String> tags){
        ArrayList<Integer> totals = new ArrayList<>();
        for (int x=0;x<tags.size();x++){
            totals.add(0);
        }
        for (int i=0; i<tasks.size(); i++){
            for (int j=0; j<tags.size(); j++) {
                if (Objects.equals(tasks.get(i).getTag(), tags.get(j))) {
                     totals.set(j, totals.get(j)+1);
                }
            }
        }
        List<DataEntry> data = new ArrayList<>();
        for (int y=0; y<tags.size(); y++) {
            data.add(new ValueDataEntry(tags.get(y), totals.get(y)));
        }

        return data;
    }

    public List<DataEntry> genDifficultyBarChart(ArrayList<TaskObj> tasks){
        int dif1=1,dif2=1,dif3=0,dif4=0,dif5=0,dif6=0,dif7=0,dif8=0,dif9=0,dif10=0;
        for (int i = 0; i<tasks.size(); i++) {
            if (tasks.get(i).getDifficulty() != null){
                switch (tasks.get(i).getDifficulty()){
                    case "1":
                        dif1 += 1;
                        break;
                    case "2":
                        dif2 += 1;
                        break;
                    case "3":
                        dif3 += 1;
                        break;
                    case "4":
                        dif4 += 1;
                        break;
                    case "5":
                        dif5 += 1;
                        break;
                    case "6":
                        dif6 += 1;
                        break;
                    case "7":
                        dif7 += 1;
                        break;
                    case "8":
                        dif8 += 1;
                        break;
                    case "9":
                        dif9 += 1;
                        break;
                    case "10":
                        dif10 += 1;
                        break;
                }
            }}

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("1", dif1));
        data.add(new ValueDataEntry("2", dif2));
        data.add(new ValueDataEntry("3", dif3));
        data.add(new ValueDataEntry("4", dif4));
        data.add(new ValueDataEntry("5", dif5));
        data.add(new ValueDataEntry("6", dif6));
        data.add(new ValueDataEntry("7", dif7));
        data.add(new ValueDataEntry("8", dif8));
        data.add(new ValueDataEntry("9", dif9));
        data.add(new ValueDataEntry("10", dif10));


        return data;
    }

    public List<DataEntry> genOnTimeData(ArrayList<TaskObj> tasks){
        int onTime=0, overTime=0, underTime=0;
        for (int i = 0; i<tasks.size(); i++) {
            if(tasks.get(i).getActualDur() != "" && tasks.get(i).getExpDur() != "" &&
                    tasks.get(i).getActualDur() != null && tasks.get(i).getExpDur() != null){
                if (Integer.parseInt(tasks.get(i).getExpDur()) > Integer.parseInt(tasks.get(i).getActualDur())){
                underTime += 1;
                }
                else if (Integer.parseInt(tasks.get(i).getExpDur()) < Integer.parseInt(tasks.get(i).getActualDur())){
                    overTime += 1;
                }
                else{onTime+=1;}
            }
        }

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("On Time", onTime));
        data.add(new ValueDataEntry("Over Time", overTime));
        data.add(new ValueDataEntry("Under Time", underTime));

        return data;
    }

    public List<DataEntry> genOnTimeScatterPoints(ArrayList<TaskObj> tasks){
        List<DataEntry> data = new ArrayList<>();
        for (int i = 0; i<tasks.size(); i++) {
            if(tasks.get(i).getActualDur() != "" && tasks.get(i).getExpDur() != "" &&
                    tasks.get(i).getActualDur() != null && tasks.get(i).getExpDur() != null){
                data.add(new ValueDataEntry(Integer.parseInt(tasks.get(i).getExpDur()),Integer.parseInt(tasks.get(i).getActualDur())));
            }
        }
        return data;
    }

    public List<DataEntry> genOnTimeScatterLine(ArrayList<TaskObj> tasks){
        List<DataEntry> data = new ArrayList<>();
        ArrayList<Integer> exp=new ArrayList<>(), act = new ArrayList<>();
        for (int i=0; i<tasks.size(); i++) {
            if (tasks.get(i).getActualDur() != "" && tasks.get(i).getExpDur() != "" &&
                    tasks.get(i).getActualDur() != null && tasks.get(i).getExpDur() != null) {
                exp.add(Integer.parseInt(tasks.get(i).getExpDur()));
                act.add(Integer.parseInt(tasks.get(i).getActualDur()));
            }
        }
        if(exp.size()>0) {
            int maxExp = Collections.max(exp);
            int maxAct = Collections.max(act);

            int maxLine;
            if (maxExp > maxAct) {
                maxLine = maxExp + 1;
            } else {
                maxLine = maxAct + 1;
            }

            data.add(new ValueDataEntry(0, 0));
            data.add(new ValueDataEntry(maxLine, maxLine));
        }

        return data;
    }


    public List<DataEntry> getCompleteVsIncompleteData() {
        return completeVsIncompleteData;
    }

    public void setCompleteVsIncompleteData(List<DataEntry> completeVsIncompleteData) {
        this.completeVsIncompleteData = completeVsIncompleteData;
    }

    public List<DataEntry> getTagsData() {
        return tagsData;
    }

    public void setTagsData(List<DataEntry> tagsData) {
        this.tagsData = tagsData;
    }

    public List<DataEntry> getDifficultyData() {
        return difficultyData;
    }

    public void setDifficultyData(List<DataEntry> difficultyData) {
        this.difficultyData = difficultyData;
    }

    public List<DataEntry> getOnTimeData() {
        return onTimeData;
    }

    public void setOnTimeData(List<DataEntry> onTimeData) {
        this.onTimeData = onTimeData;
    }

    public void setOnTimeScatterPoints(List<DataEntry> onTimeData) {
        this.onTimeScatterPoints = onTimeData;
    }

    public List<DataEntry> getOnTimeScatterPoints() {
        return onTimeScatterPoints;
    }


    public List<DataEntry> getOnTimeScatterLine() {
        return onTimeScatterLine;
    }

    public void setOnTimeScatterLine(List<DataEntry> onTimeData) {
        this.onTimeScatterLine = onTimeScatterLine;
    }
}


