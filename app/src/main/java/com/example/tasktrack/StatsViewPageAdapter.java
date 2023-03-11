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
import com.anychart.charts.Scatter;
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.scatter.series.Line;
import com.anychart.core.scatter.series.Marker;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.SolidFill;
import com.anychart.graphics.vector.text.HAlign;

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
                tagsPie.background().stroke("5 #EDE21C");
                tagsPie.background().fill("#1E838C");
                holder.anyChartView.setChart(tagsPie);
                holder.pageGraphTitle.setText("Task Type Proportions");
                break;
            case completeVsIncompletePie:
                Pie compVsIncompPie = AnyChart.pie();
                List<DataEntry> compVsIncompData = stats.getCompleteVsIncompleteData();
                compVsIncompPie.data(compVsIncompData);
                compVsIncompPie.background().stroke("5 #EDE21C");
                compVsIncompPie.background().fill("#1E838C");
                holder.anyChartView.setChart(compVsIncompPie);
                holder.pageGraphTitle.setText("Task Complete Vs Incomplete");
                break;
            case difficultyBar:
                Cartesian difficultyCartesian = AnyChart.cartesian();
                List<DataEntry> difficultyData = stats.getDifficultyData();
                Column column = difficultyCartesian.column(difficultyData);
                difficultyCartesian.xAxis(0).title("Difficulty");
                difficultyCartesian.yAxis(0).title("Frequency");
                difficultyCartesian.background().stroke("5 #EDE21C");
                difficultyCartesian.background().fill("#1E838C");
                holder.anyChartView.setChart(difficultyCartesian);
                holder.pageGraphTitle.setText("Task Difficulty Spread");
                break;
            case onTimePie:
                Pie onTimePie = AnyChart.pie();
                List<DataEntry> onTimePieData = stats.getOnTimeData();
                onTimePie.data(onTimePieData);
                onTimePie.background().stroke("5 #EDE21C");
                onTimePie.background().fill("#1E838C");
                holder.anyChartView.setChart(onTimePie);
                holder.pageGraphTitle.setText("Task Completed On Time Or Over Time");
                break;
            case onTimeScatterPlot:
                Scatter onTimeScatter = AnyChart.scatter();
                List<DataEntry> onTimeScatterPoints = stats.getOnTimeScatterPoints();
                onTimeScatter.yAxis(0).title("Actual Duration (Mins)");
                onTimeScatter.xAxis(0)
                        .title("Expected Duration (Mins)")
                        .drawFirstLabel(false)
                        .drawLastLabel(false);
                onTimeScatter.interactivity()
                        .hoverMode(HoverMode.BY_SPOT)
                        .spotRadius(30d);
                Marker marker = onTimeScatter.marker(stats.getOnTimeScatterPoints());
                marker.type(MarkerType.TRIANGLE_UP)
                        .size(4d);
                marker.hovered()
                        .size(7d)
                        .fill(new SolidFill("gold", 1d))
                        .stroke("anychart.color.darken(gold)");
                marker.tooltip()
                        .hAlign(HAlign.START)
                        .format("Actual Duration: {%Value} mins.\\nExpected Duration: {%X} mins.");
                Line onTimeScatterLine = onTimeScatter.line(stats.getOnTimeScatterLine());
                onTimeScatter.background().stroke("5 #EDE21C");
                onTimeScatter.background().fill("#1E838C");
                holder.anyChartView.setChart(onTimeScatter);
                holder.pageGraphTitle.setText("Expected vs Actual Task Times");
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
