package com.example.tasktrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StatsViewPageAdapter extends RecyclerView.Adapter<StatsViewPageAdapter.ViewHolder> {
    private Context context;
    private int pageId;
    private ArrayList<graphs> graphs;
    private ArrayList<TaskObj> tasks;
    private Stats stats;


    RecycleViewInterface recycleViewInterface;

    public StatsViewPageAdapter(Context context, int pageId, ArrayList<graphs> graphs, ArrayList<TaskObj> tasks, Stats stats,RecycleViewInterface recycleViewInterface){
        this.context = context;
        this.pageId = pageId;
        this.graphs = graphs;
        this.recycleViewInterface = recycleViewInterface;
        this.tasks = tasks;
        this.stats = stats;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(this.context).inflate(this.pageId, parent, false);
        return new ViewHolder(v, recycleViewInterface);
    }

    @Override
    //TODO
    //Try calculating stats and figures in onCreate and not the adapter
    //Adapter Binding seems to be taking too long and not finishing.
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int pos) {
        System.out.println(pos+"====");
        switch (graphs.get(pos)){
            case tagsPie:
                Pie tagsPie = AnyChart.pie();
                List<DataEntry> data = stats.getTagsData();
                tagsPie.data(data);
                holder.anyChartView.setChart(tagsPie);
                holder.pageGraphTitle.setText("Task Type Proportions");
                break;
            case completeVsIncompletePie:
                Pie compVsIncompPie = AnyChart.pie();
                List<DataEntry> compVsIncompData = stats.getCompleteVsIncompleteData();
                compVsIncompPie.data(compVsIncompData);
                holder.anyChartView.setChart(compVsIncompPie);
                holder.pageGraphTitle.setText("Task Complete Vs Incomplete");
                break;
            case difficultyBar:
                Cartesian difficultyCartesian = AnyChart.cartesian();
                List<DataEntry> difficultyData = stats.getDifficultyData();
                Column column = difficultyCartesian.column(difficultyData);
                holder.anyChartView.setChart(difficultyCartesian);
                holder.pageGraphTitle.setText("Task Difficulty Spread");
                break;
            case onTimePie:
                Pie onTimePie = AnyChart.pie();
                List<DataEntry> onTimePieData = stats.getOnTimeData();
                onTimePie.data(onTimePieData);
                holder.anyChartView.setChart(onTimePie);
                holder.pageGraphTitle.setText("Task Completed On Time Or Over Time");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return graphs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView pageGraphTitle;
        public AnyChartView anyChartView;


        public ViewHolder(@androidx.annotation.NonNull View itemView, RecycleViewInterface recycleViewInterface) {
            super(itemView);
            pageGraphTitle = itemView.findViewById(R.id.graphTitle);
            anyChartView = itemView.findViewById(R.id.any_chart_view);

        }

    }

    private void expectedVsActualTimes(ViewHolder holder){}

}
